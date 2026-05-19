package org.jarvis.settings.llmSettings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户大模型动态配置表
 * </p>
 *
 * @author hspro
 * @since 2026-05-14
 */
@Getter
@Setter
@ToString
@TableName("j_llm_config")
public class JLlmConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 供应商标识 (如 openai, deepseek, ollama)
     */
    @TableField("provider")
    private String provider;

    /**
     * API_KEY (强烈建议后端加密后存储)
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 代理地址 (如 https://api.deepseek.com/v1)
     */
    @TableField("base_url")
    private String baseUrl;

    /**
     * 模型名称 (如 gpt-4o, deepseek-chat)
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 温度参数(0.0-2.0)，控制随机性
     */
    @TableField("temperature")
    private BigDecimal temperature;

    /**
     * 是否启用 (1-启用, 0-禁用)
     */
    @TableField("is_active")
    private Boolean isActive;
}
