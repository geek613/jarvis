package org.jarvis.bi.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.bi.domain.JBiGroup;
import org.jarvis.bi.service.IJBiGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 报表分组表 (用户隔离版) 前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@RestController
@RequestMapping("/jBiGroup")
public class JBiGroupController {
    @Autowired
    private IJBiGroupService jBiGroupService;

    /**
     * 获取分组列表
     */
    @GetMapping("/list")
    public JarvisResult list(JBiGroup group) {
        List<JBiGroup> list = jBiGroupService.queryList(group);
        return JarvisResult.success(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public JarvisResult getById(@PathVariable("id") Long id) {
        return JarvisResult.success(jBiGroupService.getById(id));
    }

    /**
     * 新增分组
     */
    @PostMapping
    public JarvisResult add(@RequestBody JBiGroup group) {
        return JarvisResult.success(jBiGroupService.save(group));
    }

    /**
     * 修改分组
     */
    @PutMapping
    public JarvisResult edit(@RequestBody JBiGroup group) {
        return JarvisResult.success(jBiGroupService.updateById(group));
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/{id}")
    public JarvisResult removeById(@PathVariable("id") Long id) {
        return JarvisResult.success(jBiGroupService.removeById(id));
    }
}