package org.jarvis.agent.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.Jarvis.common.utils.StringUtils;
import org.Jarvis.common.contants.Constants;
import org.jarvis.agent.chat.domain.JChatMessages;
import org.jarvis.agent.chat.mapper.JChatMessagesMapper;
import org.jarvis.agent.chat.service.IJChatMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jarvis.agent.feign.JarvisSettingsServiceFeignClient;
import org.jarvis.api.dto.JLlmConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JarvisSettingsServiceFeignClient settingsServiceFeignClient;

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

    @Override
    public JLlmConfigDto getUserLlmConfig(Long userId) {
        String key = Constants.JLLM_CONFIG_kEY + userId;

        // 拉取配置
        JLlmConfigDto userLlmConfig = settingsServiceFeignClient.getUserLlmConfig(userId);
        if(userLlmConfig == null){

        }
        return null;
    }
}
