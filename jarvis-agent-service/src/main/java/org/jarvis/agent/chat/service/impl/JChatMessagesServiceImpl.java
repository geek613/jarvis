package org.jarvis.agent.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.Jarvis.common.StringUtils;
import org.jarvis.agent.chat.domain.JChatMessages;
import org.jarvis.agent.chat.mapper.JChatMessagesMapper;
import org.jarvis.agent.chat.service.IJChatMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-13
 */
@Service
public class JChatMessagesServiceImpl extends ServiceImpl<JChatMessagesMapper, JChatMessages> implements IJChatMessagesService {

    @Override
    public List<JChatMessages> queryList(JChatMessages entity) {
        LambdaQueryWrapper<JChatMessages> qw = new LambdaQueryWrapper<>();
        if (entity.getUserId() != null) {
            qw.eq(JChatMessages::getUserId, entity.getUserId());
        }
        if (StringUtils.isNotEmpty(entity.getChatId())) {
            qw.eq(JChatMessages::getChatId, entity.getChatId());
        }
        if (StringUtils.isNotEmpty(entity.getType())) {
            qw.eq(JChatMessages::getType, entity.getType());
        }
        if (StringUtils.isNotEmpty(entity.getText())) {
            qw.like(JChatMessages::getText, entity.getText());
        }
        qw.orderByDesc(JChatMessages::getId);
        return this.list(qw);
    }
}
