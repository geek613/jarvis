package org.jarvis.auth.system.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.auth.service.IJSysUserService;
import org.jarvis.auth.system.domain.dto.LoginReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class LoginController {
    @Autowired
    private IJSysUserService jSysUserService;

    /**
     * 登录
     * @param loginReq
     * @return
     */
    @PostMapping("/login")
    public JarvisResult<String> login(@RequestBody LoginReq loginReq) {
        /**
         * TODO 密码可以加解密，这里留给你去做
         */
        return jSysUserService.login(loginReq.getUsername(), loginReq.getPassword());
    }
}
