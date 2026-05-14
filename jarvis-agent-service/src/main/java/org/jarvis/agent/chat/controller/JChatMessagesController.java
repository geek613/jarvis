package org.jarvis.agent.chat.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.agent.chat.domain.JChatMessages;
import org.jarvis.agent.chat.service.IJChatMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hspro
 * @since 2026-05-13
 */
@RestController
@RequestMapping("/jChatMessages")
public class JChatMessagesController {
    @Autowired
    private IJChatMessagesService chatMessagesService;

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public JarvisResult list(JChatMessages entity) {
        List<JChatMessages> list = chatMessagesService.queryList(entity);
        return JarvisResult.success(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public JarvisResult getById(@PathVariable("id") Integer id) {
        return JarvisResult.success(chatMessagesService.getById(id));
    }

    /**
     * 新增消息
     */
    @PostMapping
    public JarvisResult add(@RequestBody JChatMessages entity) {
        return JarvisResult.success(chatMessagesService.save(entity));
    }

    /**
     * 修改消息
     */
    @PutMapping
    public JarvisResult edit(@RequestBody JChatMessages entity) {
        return JarvisResult.success(chatMessagesService.updateById(entity));
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public JarvisResult removeById(@PathVariable("id") Integer id) {
        return JarvisResult.success(chatMessagesService.removeById(id));
    }
}