package org.jarvis.agent.chat.service;

import org.jarvis.agent.chat.domain.JChatMessages;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jarvis.api.dto.JLlmConfigDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hspro
 * @since 2026-05-13
 */
public interface IJChatMessagesService extends IService<JChatMessages> {

    List<JChatMessages> queryList(JChatMessages entity);
    public JLlmConfigDto getUserLlmConfig(Long userId);
}
