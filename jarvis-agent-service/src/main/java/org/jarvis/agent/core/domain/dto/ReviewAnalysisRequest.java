package org.jarvis.agent.core.domain.dto;

import lombok.Data;

@Data
public class ReviewAnalysisRequest {
    /** 用户 ID */
    private Long userId;
    /** Excel 文件的云端访问地址 */
    private String fileUrl;
    /** 返回前 N 个热词，默认 30 */
    private Integer topN;
}
