package org.jarvis.auth.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.Jarvis.common.domain.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Jarvis系统用户表
 * </p>
 *
 * @author hspro
 * @since 2026-05-07
 */
@Getter
@Setter
@ToString
@TableName("j_sys_user")
public class JSysUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 登录密码(加密后)
     */
    @TableField("password")
    private String password;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 账号状态(1:正常, 0:停用)
     */
    @TableField("status")
    private Boolean status;

    /**
     * Milvus中的向量ID，用于刷脸登录关联
     */
    @TableField("face_id")
    private String faceId;
}
