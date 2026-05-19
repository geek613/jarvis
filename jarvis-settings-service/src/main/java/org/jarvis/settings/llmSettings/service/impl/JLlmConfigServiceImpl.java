package org.jarvis.settings.llmSettings.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.Jarvis.common.utils.StringUtils;
import org.jarvis.settings.llmSettings.domain.JLlmConfig;
import org.jarvis.settings.llmSettings.mapper.JLlmConfigMapper;
import org.jarvis.settings.llmSettings.service.IJLlmConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户大模型动态配置表 服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-14
 */
@Service
public class JLlmConfigServiceImpl extends ServiceImpl<JLlmConfigMapper, JLlmConfig> implements IJLlmConfigService {

    @Override
    public List<JLlmConfig> queryList(JLlmConfig config) {
        LambdaQueryWrapper<JLlmConfig> qw = new LambdaQueryWrapper<>();
        if (config.getUserId() != null) {
            qw.eq(JLlmConfig::getUserId, config.getUserId());
        }
        if (StringUtils.isNotEmpty(config.getProvider())) {
            qw.eq(JLlmConfig::getProvider, config.getProvider());
        }
        if (StringUtils.isNotEmpty(config.getModelName())) {
            qw.like(JLlmConfig::getModelName, config.getModelName());
        }
        if (config.getIsActive() != null) {
            qw.eq(JLlmConfig::getIsActive, config.getIsActive());
        }
        return this.list(qw);
    }
}
