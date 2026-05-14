package org.jarvis.agent.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jarvis.agent.chat.domain.JChatMessages;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hspro
 * @since 2026-05-13
 */
@Mapper
public interface JChatMessagesMapper extends BaseMapper<JChatMessages> {

}
