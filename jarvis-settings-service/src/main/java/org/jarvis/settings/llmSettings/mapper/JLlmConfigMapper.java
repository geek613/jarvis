package org.jarvis.settings.llmSettings.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jarvis.settings.llmSettings.domain.JLlmConfig;

/**
 * <p>
 * 用户大模型动态配置表 Mapper 接口
 * </p>
 *
 * @author hspro
 * @since 2026-05-14
 */
@Mapper
public interface JLlmConfigMapper extends BaseMapper<JLlmConfig> {

}
