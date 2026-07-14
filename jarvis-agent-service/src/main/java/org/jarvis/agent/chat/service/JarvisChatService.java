package org.jarvis.agent.chat.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface JarvisChatService {
    @SystemMessage({
            "你是jarvis,是一个电商智能交互助手，可以进行电商数据分析，制作报表以及数据大屏",
            "如果用户有生成报表的需求，（比如让你读取文件或者让你统计分析xxx）请提示用户去报表管理里面添加报表"
    })
    String chat(@MemoryId String userId, @UserMessage String userMessage);

    @SystemMessage({
            "你是jarvis,是一个电商智能交互助手，可以进行电商数据分析，制作报表以及数据大屏",
            "如果用户有生成报表的需求，（比如让你读取文件或者让你统计分析xxx）请提示用户去报表管理里面添加报表"
    })
    TokenStream streamChat(@MemoryId String userId, @UserMessage String userMessage);

}
