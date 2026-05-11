package org.jarvis.agent.factory;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct; // 如果是旧版Spring Boot(2.x)，请换成 javax.annotation.PostConstruct
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class AiServiceFactory {

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${deepseek.thinking}")
    private String thinking;

    /**
     * -- GETTER --
     *  获取chatModel
     */
    // 将模型实例作为单例成员变量保留，提升性能
    @Getter
    private OpenAiChatModel chatModel;

    /**
     * 在 Spring 注入完 @Value 属性后，自动初始化底层模型
     */
    @PostConstruct
    public void initModel() {
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("type", thinking);

        this.chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .customParameters(Map.of("thinking", thinkingConfig))
                .responseFormat("json_object")
                .logRequests(true)
                .logResponses(true)
                .build();

        log.info("DeepSeek OpenAiChatModel 初始化完成，模型：{}", modelName);
    }

    /**
     * 基础方法：无 Tools
     */
    public <T> T createService(Class<T> serviceClass) {
        return AiServices.builder(serviceClass)
                .chatModel(chatModel) // 标准API通常是 chatLanguageModel，部分版本可能是 chatModel
                .build();
    }

    /**
     * 重载方法 1：传入单个或多个 Tool 实例对象 (Object...)
     * 适用场景：你已经拿到了工具类的实例，直接传进来
     *
     * @param serviceClass AiService 接口类
     * @param tools        带有 @Tool 注解的实例对象 (支持 1个 或 逗号分隔的多个)
     */
    public <T> T createService(Class<T> serviceClass, Object... tools) {
        return AiServices.builder(serviceClass)
                .chatModel(chatModel)
                .tools(tools) // LangChain4j 原生支持传入 Object 数组
                .build();
    }

    /**
     * 重载方法 2：传入 Tool 实例集合 (Collection<Object>)
     * 适用场景：工具实例存放在 List 或 Set 中
     *
     * @param serviceClass AiService 接口类
     * @param tools        工具实例的集合
     */
    public <T> T createService(Class<T> serviceClass, Collection<Object> tools) {
        // LangChain4j 的 tools 方法支持 List<Object>
        List<Object> toolList = tools instanceof List ? (List<Object>) tools : new ArrayList<>(tools);
        return AiServices.builder(serviceClass)
                .chatModel(chatModel)
                .tools(toolList)
                .build();
    }
}