package org.jarvis.settings.llmSettings.service;

import org.jarvis.settings.llmSettings.domain.JLlmConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户大模型动态配置表 服务类
 * </p>
 *
 * @author hspro
 * @since 2026-05-14
 */
public interface IJLlmConfigService extends IService<JLlmConfig> {

    List<JLlmConfig> queryList(JLlmConfig config);
}
