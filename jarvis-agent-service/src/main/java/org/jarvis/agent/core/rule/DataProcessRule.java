package org.jarvis.agent.core.rule;

import lombok.Data;

import java.util.List;
@Data
public class DataProcessRule {

    // 处理模式："AGGREGATE" (需要分组求和/计数的图表，如柱状图、饼图) 
    //         "EXTRACT"   (只需要提取明细数据的图表，如散点图、原始数据表)
    private String mode;

    // 过滤条件列表（支持多个条件并集/交集）
    private List<Filter> filters;

    // 维度列（通常对应图表的 X 轴，或分类依据）
    private List<String> dimensions;

    // 指标列（通常对应图表的 Y 轴，需要被计算的数值列）
    private List<Metric> metrics;

    // --- 内部类定义 ---
    public static class Filter {
        public String field;      // 字段名
        public String operator;   // 操作符：EQUALS, CONTAINS, GREATER_THAN 等
        public String value;      // 比较值
    }

    public static class Metric {
        public String field;      // 需要处理的数值字段
        public String operation;  // 操作类型：SUM, COUNT, AVG, MAX, MIN (如果是 EXTRACT 模式，此项忽略)
    }
}