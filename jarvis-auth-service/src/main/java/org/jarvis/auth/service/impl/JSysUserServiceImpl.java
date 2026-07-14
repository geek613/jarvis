package org.jarvis.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.result.JarvisResult;
import org.Jarvis.common.utils.StringUtils;
import org.jarvis.auth.domain.JSysUser;
import org.jarvis.auth.mapper.JSysUserMapper;
import org.jarvis.auth.service.IJSysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jarvis.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Jarvis系统用户表 服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-07
 */
@Slf4j
@Service
public class JSysUserServiceImpl extends ServiceImpl<JSysUserMapper, JSysUser> implements IJSysUserService {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public JarvisResult<String> login(String username, String password) {
        List<JSysUser> list = baseMapper.selectList(new QueryWrapper<JSysUser>().eq("username", username).eq("password", password));
        if(list.isEmpty()){
            log.info("用户 {} 登录失败，用户名或密码错误", username);
            return JarvisResult.error();
        }
        JSysUser user = list.get(0);
        // 调用工具类生成 Token
        String token = jwtUtils.generateToken(String.valueOf(user.getUserId()), user.getUsername());
        JarvisResult<String> res = new JarvisResult<>();
        res.setCode(200);
        res.setMessage("登录成功");
        res.setData(token);
        log.info("用户 {} 登录成功，生成 Token：{}", username, token);
        return res;
    }

    @Override
    public JarvisResult<String> register(String username, String password) {
        List<JSysUser> list = baseMapper.selectList(new QueryWrapper<JSysUser>().eq("username", username));
        if (!list.isEmpty()) {
            return JarvisResult.error("用户名已存在");
        }
        JSysUser user = new JSysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(true);
        baseMapper.insert(user);
        return JarvisResult.success("注册成功");
    }

    @Override
    public List<JSysUser> queryList(JSysUser user) {
        LambdaQueryWrapper<JSysUser> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(user.getUsername())) {
            qw.like(JSysUser::getUsername, user.getUsername());
        }
        if (StringUtils.isNotEmpty(user.getEmail())) {
            qw.like(JSysUser::getEmail, user.getEmail());
        }
        if (StringUtils.isNotEmpty(user.getPhone())) {
            qw.like(JSysUser::getPhone, user.getPhone());
        }
        if (user.getStatus() != null) {
            qw.eq(JSysUser::getStatus, user.getStatus());
        }
        qw.orderByAsc(JSysUser::getUserId);
        return this.list(qw);
    }
}
