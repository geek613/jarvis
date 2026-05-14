package org.jarvis.bi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.Jarvis.common.domain.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 报表管理明细表
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@Getter
@Setter
@ToString
@TableName(value = "j_bi_report",autoResultMap = true)
public class JBiReport extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报表名称
     */
    @TableField("report_name")
    private String reportName;

    /**
     * 所属分组ID (关联 j_bi_group.id)
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 归属用户ID (数据隔离)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报表图表类型 (bar:柱状图, line:折线图, pie:饼图, scatter:散点图, table:数据表)
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 数据源ID (如果是直连数据库查询的，关联数据源配置表)
     */
    @TableField("data_source_id")
    private Long dataSourceId;

    /**
     * 用于生成该报表的 SQL 查询语句 (如果用户手写SQL的话)
     */
    @TableField("query_sql")
    private String querySql;

    /**
     * 图表的渲染配置 (如 Echarts 的 option JSON 字符串，包含颜色、图例、X轴Y轴映射等)
     */
    // 2. 将类型定义为 Map 或 JSONObject
    // 3. 指定使用 JacksonTypeHandler
    @TableField(value = "chart_config", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> chartConfig;

    /**
     * 组内排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 报表状态 (1-启用，0-禁用/草稿)
     */
    @TableField("status")
    private Boolean status;

    /**
     * 报表备注描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 逻辑删除(0-未删除，1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

}
