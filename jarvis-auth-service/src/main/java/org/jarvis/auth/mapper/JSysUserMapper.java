package org.jarvis.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jarvis.auth.domain.JSysUser;

/**
 * <p>
 * Jarvis系统用户表 Mapper 接口
 * </p>
 *
 * @author hspro
 * @since 2026-05-07
 */
@Mapper
public interface JSysUserMapper extends BaseMapper<JSysUser> {

}
