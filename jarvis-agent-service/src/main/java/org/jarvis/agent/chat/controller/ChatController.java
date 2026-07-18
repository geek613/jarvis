package org.jarvis.agent.chat.controller;

import com.google.gson.Gson;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.agent.chat.domain.dto.JChatDto;
import org.jarvis.agent.chat.service.JarvisChatService;
import org.jarvis.agent.factory.AiServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/jarvis")
public class ChatController {
    @Autowired
    private AiServiceFactory aiServiceFactory;

    @Autowired
    private Gson gson;

    @GetMapping("/chat")
    public JarvisResult<String> chat(JChatDto chat) {
        long userId = chat.getUserId();
        log.info("用户 {} 输入：{}", userId, chat.getMessage());
        JarvisChatService service = aiServiceFactory.createService(JarvisChatService.class, true, userId);
        return JarvisResult.success(service.chat(chat.getMemoryId(), chat.getMessage()));
    }

    /**
     * 流式输出
     */
    @GetMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(JChatDto chat) {
        long userId = chat.getUserId();
        SseEmitter emitter = new SseEmitter(0L);
        JarvisChatService service = aiServiceFactory.createService(JarvisChatService.class, true, true, userId);
        TokenStream tokenStream = service.streamChat(chat.getMemoryId(), chat.getMessage());
        tokenStream
                .onPartialResponse(token -> {
                    try {
                        JarvisResult<String> result = JarvisResult.success(token);
                        emitter.send(SseEmitter.event().data(gson.toJson(result)));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(response -> {
                    try {
                        JarvisResult<String> endResult = JarvisResult.success("[DONE]");
                        emitter.send(SseEmitter.event().data(gson.toJson(endResult)));
                    } catch (Exception e) {
                        // log error
                    } finally {
                        emitter.complete();
                    }
                })
                .onError(error -> {
                    try {
                        JarvisResult<String> errorResult = JarvisResult.error("调用失败: " + error.getMessage());
                        emitter.send(SseEmitter.event().data(gson.toJson(errorResult)));
                    } catch (Exception e) {
                        log.error("SSE发送错误失败", e);
                    } finally {
                        emitter.completeWithError(error);
                    }
                })
                .start();

        return emitter;
    }


}
