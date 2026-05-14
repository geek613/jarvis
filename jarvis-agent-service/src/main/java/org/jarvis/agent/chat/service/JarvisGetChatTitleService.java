package org.jarvis.agent.chat.service;

import dev.langchain4j.service.SystemMessage;

public interface JarvisGetChatTitleService {
    @SystemMessage({
            "你可以根据用户问题自动生成一个简短的（尽量保持在15-20字以内）对话标题",
            "请直接返回一个标题，不需要多余内容"
    })
    String getChatTitle(String userMessage);
}