package org.jarvis.agent.factory;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct; // 如果是旧版Spring Boot(2.x)，请换成 javax.annotation.PostConstruct
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jarvis.agent.core.store.PersistentChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PersistentChatMemoryStore store;

    @Autowired
    private AiModelFactory aiModelFactory;

    // 将模型实例作为单例成员变量保留，提升性能
    @Getter
    private OpenAiChatModel chatModel;

    @Getter
    private OpenAiStreamingChatModel streamChatModel;

    private ChatMemoryProvider chatMemoryProvider;

    /**
     * 在 Spring 注入完 @Value 属性后，自动初始化底层模型
     */
    @PostConstruct
    public void initChatModel() {
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("type", thinking);
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .customParameters(Map.of("thinking", thinkingConfig))
                .logRequests(true)
                .logResponses(true)
                .build();
        log.info("DeepSeek OpenAiChatModel 初始化完成，模型：{}", modelName);
    }

    @PostConstruct
    public void initStreamChatModel() {
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("type", thinking);
        this.streamChatModel = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .customParameters(Map.of("thinking", thinkingConfig))
                .logRequests(true)
                .logResponses(true)
                .build();

        log.info("DeepSeek streamChatModel 初始化完成，模型：{}", modelName);
    }

    @PostConstruct
    public void initChatMemoryProvider() {
        this.chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
    }

    /**
     * 基础方法：无 Tools
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory) {
        if(enableMemory){
            return AiServices.builder(serviceClass)
                    .chatModel(chatModel)
                    .chatMemoryProvider(chatMemoryProvider)
                    .build();
        }
        return AiServices.builder(serviceClass)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 基础方法：无 Tools，可选择是否开启流式输出
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory, boolean enableStreaming) {
        AiServices<T> builder = AiServices.builder(serviceClass);
        if (enableStreaming) {
            builder.streamingChatModel(streamChatModel);
        } else {
            builder.chatModel(chatModel);
        }
        if (enableMemory) {
            builder.chatMemoryProvider(chatMemoryProvider);
        }
        return builder.build();
    }


    /**
     * 根据 userId 加载用户自定义模型创建 Service（非流式）
     * 用户未配置时抛出异常
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory, long userId) {
        return createService(serviceClass, enableMemory, false, userId);
    }

    /**
     * 根据 userId 加载用户自定义模型创建 Service，可选择流式输出
     * 用户未配置时抛出异常
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory, boolean enableStreaming, long userId) {
        OpenAiChatModel userChatModel = aiModelFactory.createChatModel(userId);
        OpenAiStreamingChatModel userStreamChatModel = aiModelFactory.createStreamingChatModel(userId);
        return createService(serviceClass, enableMemory, enableStreaming, userChatModel, userStreamChatModel);
    }

    /**
     * 使用自定义模型创建 Service（同时传入 chatModel 和 streamChatModel）
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory, boolean enableStreaming,
                               OpenAiChatModel customChatModel, OpenAiStreamingChatModel customStreamChatModel) {
        AiServices<T> builder = AiServices.builder(serviceClass);
        if (enableStreaming) {
            builder.streamingChatModel(customStreamChatModel != null ? customStreamChatModel : streamChatModel);
        } else {
            builder.chatModel(customChatModel != null ? customChatModel : chatModel);
        }
        if (enableMemory) {
            builder.chatMemoryProvider(chatMemoryProvider);
        }
        return builder.build();
    }

    /**
     * 根据 userId 加载用户自定义模型创建 Service，带 Tools
     * 用户未配置时抛出异常
     */
    public <T> T createService(Class<T> serviceClass, boolean enableMemory, long userId, Object... tools) {
        OpenAiChatModel userChatModel = aiModelFactory.createChatModel(userId);
        AiServices<T> builder = AiServices.builder(serviceClass);
        builder.chatModel(userChatModel);
        if (enableMemory) {
            builder.chatMemoryProvider(chatMemoryProvider);
        }
        builder.tools(tools);
        return builder.build();
    }

    /**
     * 传入 Tool 实例集合 (Collection<Object>)，使用用户自定义模型
     *
     * @param serviceClass AiService 接口类
     * @param tools        工具实例的集合
     * @param userId       用户ID
     */
    public <T> T createService(Class<T> serviceClass, long userId, Collection<Object> tools) {
        OpenAiChatModel userChatModel = aiModelFactory.createChatModel(userId);
        List<Object> toolList = tools instanceof List ? (List<Object>) tools : new ArrayList<>(tools);
        return AiServices.builder(serviceClass)
                .chatModel(userChatModel)
                .tools(toolList)
                .build();
    }
}