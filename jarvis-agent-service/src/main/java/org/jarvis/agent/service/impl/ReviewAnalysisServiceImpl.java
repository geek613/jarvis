package org.jarvis.agent.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.exception.BusinessException;
import org.jarvis.agent.core.reader.ExcelReader;
import org.jarvis.agent.factory.AiServiceFactory;
import org.jarvis.agent.core.agent.commentAgent.ReviewHotwordAgent;
import org.jarvis.agent.core.domain.KeywordCount;
import org.jarvis.agent.core.domain.dto.ReviewAnalysisResult;
import org.jarvis.agent.service.ReviewAnalysisService;
import org.jarvis.agent.core.util.ReviewBatchSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 评论热词分析服务实现
 *
 * @author hspro
 * @since 2026-07-14
 */
@Slf4j
@Service
public class ReviewAnalysisServiceImpl implements ReviewAnalysisService {

    @Autowired
    private ExcelReader excelReader;

    @Autowired
    private AiServiceFactory aiServiceFactory;

    @Autowired
    private Gson gson;

    /** 每批评论条数，默认 50 */
    @Value("${review.batch-size:50}")
    private int batchSize;

    /** 线程池核心大小，默认 4 */
    @Value("${review.thread-pool.core-size:4}")
    private int corePoolSize;

    /** 线程池最大大小，默认 8 */
    @Value("${review.thread-pool.max-size:8}")
    private int maxPoolSize;

    /** 线程池队列容量，默认 100 */
    @Value("${review.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    /** 单批调用超时（秒），默认 120 */
    @Value("${review.batch-timeout-seconds:120}")
    private int batchTimeoutSeconds;

    @Override
    public ReviewAnalysisResult analyze(String fileUrl, int topN, long userId) {
        // 1. 读取 Excel 文件
        List<Map<String, Object>> rows = excelReader.readExcelAsList(fileUrl);
        if (rows == null || rows.isEmpty()) {
            throw new BusinessException("Excel 文件内容为空，请检查文件");
        }

        // 2. 获取表头，定位"商品评论"列
        Map<String, Object> firstRow = rows.get(0);
        if (!firstRow.containsKey("商品评论")) {
            throw new BusinessException("Excel 中未找到'商品评论'列，请检查表头格式（要求第一列表头为'商品标题'，第二列表头为'商品评论'）");
        }

        // 3. 提取"商品评论"列的所有非空文本
        List<String> reviews = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Object commentObj = row.get("商品评论");
            if (commentObj != null) {
                String comment = commentObj.toString().trim();
                if (!comment.isEmpty()) {
                    reviews.add(comment);
                }
            }
        }

        if (reviews.isEmpty()) {
            throw new BusinessException("'商品评论'列中未找到任何有效评论内容，请检查文件");
        }

        int totalReviews = reviews.size();
        log.info("评论分析开始: userId={}, fileUrl={}, totalReviews={}, topN={}", userId, fileUrl, totalReviews, topN);

        // 4. 按批次拆分
        List<String> batches = ReviewBatchSplitter.split(reviews, batchSize);
        log.info("评论分为 {} 批处理，每批 {} 条", batches.size(), batchSize);

        // 5. 创建线程池并发处理各批次
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 存储每批的 Future 结果，key 为批次索引
        List<Future<List<KeywordCount>>> futures = new ArrayList<>();
        for (int i = 0; i < batches.size(); i++) {
            final int batchIndex = i;
            final String batchText = batches.get(i);
            Future<List<KeywordCount>> future = executor.submit(() -> {
                log.info("批次 {}/{} 开始处理，评论数: {}", batchIndex + 1, batches.size(),
                        batchText.split("\n").length);
                return processBatch(batchText, userId, batchIndex, batches.size());
            });
            futures.add(future);
        }

        // 6. 收集所有批次结果，合并词频
        Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                List<KeywordCount> batchResult = futures.get(i).get(batchTimeoutSeconds, TimeUnit.SECONDS);
                if (batchResult != null) {
                    for (KeywordCount kc : batchResult) {
                        wordCountMap.merge(kc.getWord(), kc.getCount(), Integer::sum);
                    }
                }
            } catch (TimeoutException e) {
                log.error("批次 {} 调用超时（{}s），跳过该批次", (i + 1), batchTimeoutSeconds, e);
            } catch (ExecutionException e) {
                log.error("批次 {} 执行失败，跳过该批次: {}", (i + 1), e.getCause().getMessage());
            } catch (InterruptedException e) {
                log.error("批次 {} 被中断，跳过该批次", (i + 1), e);
                Thread.currentThread().interrupt();
            }
        }

        // 7. 关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 8. 按频次从高到低排序，取前 topN
        List<KeywordCount> sortedKeywords = wordCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN > 0 ? topN : 30)
                .map(entry -> new KeywordCount(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 9. 组装结果
        ReviewAnalysisResult result = new ReviewAnalysisResult();
        result.setTotalReviews(totalReviews);
        result.setKeywords(sortedKeywords);

        log.info("评论分析完成: totalReviews={}, keywordCount={}", totalReviews, sortedKeywords.size());
        return result;
    }

    /**
     * 处理单个批次的评论，调用大模型提取热词
     */
    private List<KeywordCount> processBatch(String batchText, long userId, int batchIndex, int totalBatches) {
        try {
            // 统计本批评论条数
            int count = batchText.split("\n").length;

            // 通过 AiServiceFactory 创建 ReviewHotwordAgent（使用用户自定义模型）
            ReviewHotwordAgent agent = aiServiceFactory.createService(ReviewHotwordAgent.class, false, userId);

            // 调用大模型
            String rawResponse = agent.extractHotwords(count, batchText);
            log.debug("批次 {}/{} 模型原始返回: {}", batchIndex + 1, totalBatches, rawResponse);

            // 清洗返回内容（去除可能的 ```json 代码块标记）
            String cleanedJson = cleanJsonResponse(rawResponse);

            // 解析 JSON
            List<KeywordCount> batchKeywords = gson.fromJson(cleanedJson,
                    new TypeToken<List<KeywordCount>>() {}.getType());

            log.info("批次 {}/{} 处理完成，提取到 {} 个关键词", batchIndex + 1, totalBatches,
                    batchKeywords != null ? batchKeywords.size() : 0);
            return batchKeywords != null ? batchKeywords : Collections.emptyList();

        } catch (Exception e) {
            log.error("批次 {}/{} 处理异常: {}", batchIndex + 1, totalBatches, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 清洗模型返回的 JSON 字符串
     * <p>模型有时会把 JSON 包在 ```json 或 ``` 代码块里，需要去除后再解析。</p>
     */
    private String cleanJsonResponse(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "[]";
        }
        String cleaned = raw.trim();

        // 去除 ```json ... ``` 或 ``` ... ``` 代码块标记
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```\\s*$", "");
            cleaned = cleaned.trim();
        }

        // 如果清洗后不是 JSON 数组开头，尝试从第一个 [ 截取
        if (!cleaned.startsWith("[")) {
            int start = cleaned.indexOf('[');
            int end = cleaned.lastIndexOf(']');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }
        }

        return cleaned.isEmpty() ? "[]" : cleaned;
    }
}
