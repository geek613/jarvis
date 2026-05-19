package org.jarvis.agent.chat.domain.dto;

import lombok.Data;

@Data
public class JChatDto {
    /** 用户Id **/
    private long userId;
    /** 记忆Id,规则userId:UUID **/
    private String memoryId;
    /** 用户输入提示词 **/
    private String message;
}
