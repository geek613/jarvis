package org.jarvis.api.dto;

import lombok.Data;

@Data
public class JLlmConfigDto {
    private Long userId;
    private String provider;
    private String baseUrl;
    private String apiKey;
    private String modelName;
}
