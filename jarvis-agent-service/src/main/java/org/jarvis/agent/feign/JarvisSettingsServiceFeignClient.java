package org.jarvis.agent.feign;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.api.dto.JLlmConfigDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "jarvis-settings-service", path = "/jLlmConfig")
public interface JarvisSettingsServiceFeignClient {
    @GetMapping("/getByUserId/{userId}")
    JLlmConfigDto getUserLlmConfig(@PathVariable("userId") Long userId);
}
