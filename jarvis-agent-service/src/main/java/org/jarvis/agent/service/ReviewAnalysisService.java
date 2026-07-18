package org.jarvis.agent.service;

import org.jarvis.agent.core.domain.dto.ReviewAnalysisResult;

/**
 * 评论热词分析服务接口
 *
 * @author hspro
 * @since 2026-07-14
 */
public interface ReviewAnalysisService {

    /**
     * 分析 Excel 中的商品评论，提取高频热词
     *
     * @param fileUrl Excel 文件的云端访问地址
     * @param topN    返回前 N 个热词
     * @param userId  用户 ID（用于按用户配置加载 LLM 模型）
     * @return 分析结果（总评论数 + 关键词频次列表）
     */
    ReviewAnalysisResult analyze(String fileUrl, int topN, long userId);
}
