package org.jarvis.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.contants.Constants;
import org.jarvis.agent.feign.JarvisSettingsServiceFeignClient;
import org.jarvis.api.dto.JLlmConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JLlmProviderService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JarvisSettingsServiceFeignClient settingsServiceFeignClient;
    private static final String KEY_PREFIX = Constants.JLLM_CONFIG_kEY;

    public JLlmConfigDto getUserLlmConfig(Long userId) {
        String key = KEY_PREFIX + userId;
        JLlmConfigDto config = (JLlmConfigDto) redisTemplate.opsForValue().get(key);
        if (config == null) {
            log.info("Redis缓存未命中，通过 Feign 调用 settings-service 获取配置, userId: {}", userId);
            config = settingsServiceFeignClient.getUserLlmConfig(userId);
            if (config == null) {
                return null;
            }
            redisTemplate.opsForValue().set(key, config, 1, TimeUnit.DAYS);
        }
        return config;
    }

}
