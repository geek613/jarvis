package org.jarvis.bi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.bi.domain.JBiReport;
import org.jarvis.bi.service.IJBiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 报表管理明细表 前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@RestController
@RequestMapping("/jBiReport")
public class JBiReportController {
    @Autowired
    private IJBiReportService jBiReportService;

    /**
     * 获取报表列表
     */
    @GetMapping("/list")
    public JarvisResult list(JBiReport report) {
        List<JBiReport> list = jBiReportService.queryList(report);
        return JarvisResult.success(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public JarvisResult getById(@PathVariable("id") Long id) {
        return JarvisResult.success(jBiReportService.getById(id));
    }

    /**
     * 新增报表
     */
    @PostMapping
    public JarvisResult add(@RequestBody JBiReport report) {
        return JarvisResult.success(jBiReportService.save(report));
    }

    /**
     * 修改报表
     */
    @PutMapping
    public JarvisResult edit(@RequestBody JBiReport report) {
        return JarvisResult.success(jBiReportService.updateById(report));
    }

    /**
     * 删除报表
     */
    @DeleteMapping("/{id}")
    public JarvisResult removeById(@PathVariable("id") Long id) {
        return JarvisResult.success(jBiReportService.removeById(id));
    }
    /**
     * 根据groupID删除报表
     */
    @DeleteMapping("/group/{groupId}")
    public JarvisResult removeByGroupId(@PathVariable("groupId") Long groupId) {
        return JarvisResult.success(jBiReportService.remove(new QueryWrapper<JBiReport>().eq("group_id", groupId)));
    }
}
