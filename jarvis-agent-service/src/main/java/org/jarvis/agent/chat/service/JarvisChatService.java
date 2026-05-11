package org.jarvis.agent.chat.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface JarvisChatService {
    @SystemMessage("你是jarvis,是一个电商智能交互助手，可以进行电商数据分析，制作报表以及数据大屏")
    String chat(String userMessage);
}
