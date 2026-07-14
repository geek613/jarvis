package org.jarvis.auth.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.auth.domain.JSysUser;
import org.jarvis.auth.service.IJSysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Jarvis系统用户表 前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-07
 */
@RestController
@RequestMapping("/jSysUser")
public class JSysUserController {

    @Autowired
    private IJSysUserService jSysUserService;

    @GetMapping("/list")
    public JarvisResult list(JSysUser user) {
        List<JSysUser> list = jSysUserService.queryList(user);
        return JarvisResult.success(list);
    }

    @GetMapping("/{userId}")
    public JarvisResult getById(@PathVariable("userId") Long userId) {
        return JarvisResult.success(jSysUserService.getById(userId));
    }

    @PostMapping
    public JarvisResult add(@RequestBody JSysUser user) {
        return JarvisResult.success(jSysUserService.save(user));
    }

    @PutMapping
    public JarvisResult edit(@RequestBody JSysUser user) {
        return JarvisResult.success(jSysUserService.updateById(user));
    }

    @DeleteMapping("/{userId}")
    public JarvisResult removeById(@PathVariable("userId") Long userId) {
        return JarvisResult.success(jSysUserService.removeById(userId));
    }
}
