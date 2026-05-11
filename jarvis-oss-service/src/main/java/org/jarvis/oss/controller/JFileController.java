package org.jarvis.oss.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.oss.domain.JFile;
import org.jarvis.oss.service.IJFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文件存储表 前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-08
 */
@RestController
@RequestMapping("/oss/file")
public class JFileController {
    @Autowired
    private IJFileService jFileService;
    /**
     * 获取文件列表
     * @return
     */
    @GetMapping("/list")
    public JarvisResult list(JFile file) {
        List<JFile> list = jFileService.queryList(file);
        return JarvisResult.success(list);
    }

    /**
     * 新增
     * @param file
     * @return
     */

    @PostMapping
    public JarvisResult add(@RequestBody JFile file) {
        boolean save = jFileService.save(file);
        return JarvisResult.success(save);
    }

    @DeleteMapping("/removeById/{id}")
    public JarvisResult removeById(@PathVariable("id") Long id) {
        boolean remove = jFileService.removeById(id);
        return JarvisResult.success(remove);
    }
}
