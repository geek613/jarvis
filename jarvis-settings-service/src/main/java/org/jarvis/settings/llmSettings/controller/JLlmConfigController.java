package org.jarvis.settings.llmSettings.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.api.dto.JLlmConfigDto;
import org.jarvis.settings.llmSettings.domain.JLlmConfig;
import org.jarvis.settings.llmSettings.service.IJLlmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户大模型动态配置表 前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-14
 */
@RestController
@RequestMapping("/jLlmConfig")
public class JLlmConfigController {
    @Autowired
    private IJLlmConfigService jLlmConfigService;

    /**
     * 获取配置列表
     */
    @GetMapping("/list")
    public JarvisResult list(JLlmConfig config) {
        List<JLlmConfig> list = jLlmConfigService.queryList(config);
        return JarvisResult.success(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public JarvisResult getById(@PathVariable("id") Long id) {
        return JarvisResult.success(jLlmConfigService.getById(id));
    }

    /**
     * 新增配置
     */
    @PostMapping
    public JarvisResult add(@RequestBody JLlmConfig config) {
        //刷入缓存
        return JarvisResult.success(jLlmConfigService.save(config));
    }

    /**
     * 修改配置
     */
    @PutMapping
    public JarvisResult edit(@RequestBody JLlmConfig config) {
        // 更新缓存
        return JarvisResult.success(jLlmConfigService.updateById(config));
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public JarvisResult removeById(@PathVariable("id") Long id) {
        return JarvisResult.success(jLlmConfigService.removeById(id));
    }
    @GetMapping("/getByUserId/{id}")
    public JLlmConfigDto getByUserId(@PathVariable("id") Long id) {
        JLlmConfig jLlmConfig = jLlmConfigService.getOne(new QueryWrapper<JLlmConfig>().eq("user_id", id));
        if (jLlmConfig == null) {
            return null;
        }
        JLlmConfigDto jLlmConfigDto = new JLlmConfigDto();
        jLlmConfigDto.setUserId(jLlmConfig.getUserId());
        jLlmConfigDto.setBaseUrl(jLlmConfig.getBaseUrl());
        jLlmConfigDto.setApiKey(jLlmConfig.getApiKey());
        jLlmConfigDto.setModelName(jLlmConfig.getModelName());
        return jLlmConfigDto;
    }
}
