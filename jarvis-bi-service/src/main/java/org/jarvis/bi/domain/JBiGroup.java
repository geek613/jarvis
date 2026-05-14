package org.jarvis.bi.domain;

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
 * 报表分组表 (用户隔离版)
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@Getter
@Setter
@ToString
@TableName("j_bi_group")
public class JBiGroup extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属用户ID (0表示系统公共分组)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分组名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 分组类型(例如: 业务分类、大屏分类)
     */
    @TableField("group_type")
    private String groupType;

    /**
     * 父级ID (0表示顶层分组)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 显示排序(数值越小越靠前)
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 分组备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 逻辑删除(0-未删除，1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
}
