package org.jarvis.agent.chat.controller;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.result.JarvisResult;
import org.apache.catalina.security.SecurityUtil;
import org.jarvis.agent.chat.service.JarvisChatService;
import org.jarvis.agent.core.agent.LeaderAgent;
import org.jarvis.agent.core.result.LeaderResult;
import org.jarvis.agent.factory.AiServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/jarvis")
public class ChatController {
    @Autowired
    private AiServiceFactory aiServiceFactory;
    @Autowired
    private LeaderAgent leaderAgent;
    @GetMapping("/chat")
    public JarvisResult<String> chat(@RequestParam ("memoryId") String memoryId,@RequestParam("message") String message) {
        log.info("用户输入：{}", message);
        JarvisChatService service = aiServiceFactory.createService(JarvisChatService.class, true);
        return JarvisResult.success(service.chat(memoryId, message));
    }

    @GetMapping("/generateChart")
    public JarvisResult<LeaderResult> generateChart(@RequestParam("message") String message) {
        return JarvisResult.success(leaderAgent.handleTask(message));
    }
}
