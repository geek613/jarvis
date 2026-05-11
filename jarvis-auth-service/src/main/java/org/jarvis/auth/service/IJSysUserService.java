package org.jarvis.auth.service;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.auth.domain.JSysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Jarvis系统用户表 服务类
 * </p>
 *
 * @author hspro
 * @since 2026-05-07
 */
public interface IJSysUserService extends IService<JSysUser> {

    JarvisResult<String> login(String username, String password);

}
