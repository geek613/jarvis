package org.jarvis.agent.core.domain.dto;

import lombok.Data;
import org.jarvis.agent.core.domain.KeywordCount;

import java.util.List;

@Data
public class ReviewAnalysisResult {
    /** 参与分析的评论总条数（非空评论数） */
    private int totalReviews;
    /** 关键词及其频次列表，按频次从高到低排序 */
    private List<KeywordCount> keywords;
}
