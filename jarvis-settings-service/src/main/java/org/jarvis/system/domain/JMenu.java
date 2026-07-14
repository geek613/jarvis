package org.jarvis.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("j_menu")
public class JMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String menuName;
    private String menuType;    // M=目录 C=菜单 F=按钮
    private String routeName;
    private String routePath;
    private String componentPath;
    private String icon;
    private Integer sortOrder;
    private String perms;
    private Integer visible;    // 0=隐藏 1=显示
    private Integer status;     // 0=停用 1=启用
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;

    @TableField(exist = false)
    private List<JMenu> children;   // 树形结构子节点
}