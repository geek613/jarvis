package org.jarvis.agent.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论文本分批工具类
 * <p>
 * 将评论列表按指定批次大小拆分，每批拼接为单个字符串供大模型分析。
 * </p>
 *
 * @author hspro
 * @since 2026-07-14
 */
public class ReviewBatchSplitter {

    /**
     * 将评论列表按批次大小拆分为多个字符串批次
     *
     * @param reviews  评论文本列表
     * @param batchSize 每批评论条数
     * @return 分批后的字符串列表，每个元素为一个批次的评论文本（带序号）
     */
    public static List<String> split(List<String> reviews, int batchSize) {
        if (batchSize <= 0) {
            batchSize = 50;
        }
        List<String> batches = new ArrayList<>();
        for (int i = 0; i < reviews.size(); i += batchSize) {
            int end = Math.min(i + batchSize, reviews.size());
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < end; j++) {
                sb.append(j + 1).append(". ").append(reviews.get(j)).append("\n");
            }
            batches.add(sb.toString());
        }
        return batches;
    }
}
