package org.jarvis.agent.factory;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.exception.BusinessException;
import org.jarvis.agent.service.JLlmProviderService;
import org.jarvis.api.dto.JLlmConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AiModelFactory {

    @Autowired
    private JLlmProviderService llmProviderService;

    @Value("${deepseek.thinking}")
    private String thinking;

    public OpenAiChatModel createChatModel(long userId) {
        JLlmConfigDto config = llmProviderService.getUserLlmConfig(userId);
        if (config == null) {
            throw new BusinessException("未配置大模型，请先前往设置页面配置");
        }
        log.info("为用户 {} 创建 ChatModel: model={}, baseUrl={}", userId, config.getModelName(), config.getBaseUrl());
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("type", thinking);
        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .baseUrl(config.getBaseUrl())
                .customParameters(Map.of("thinking", thinkingConfig))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    public OpenAiStreamingChatModel createStreamingChatModel(long userId) {
        JLlmConfigDto config = llmProviderService.getUserLlmConfig(userId);
        if (config == null) {
            throw new IllegalStateException("未配置大模型，请先前往设置页面配置");
        }
        log.info("为用户 {} 创建 StreamingChatModel: model={}, baseUrl={}", userId, config.getModelName(), config.getBaseUrl());
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("type", thinking);
        return OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .baseUrl(config.getBaseUrl())
                .customParameters(Map.of("thinking", thinkingConfig))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
