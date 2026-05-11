package org.jarvis.agent.core.engine;

import lombok.extern.slf4j.Slf4j;
import org.jarvis.agent.core.rule.DataProcessRule;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DataProcessRuleEngine {

    public List<Map<String, Object>> execute(List<Map<String, Object>> fullData, DataProcessRule rule) {
        if (fullData == null || fullData.isEmpty() || rule == null) return new ArrayList<>();
        Stream<Map<String, Object>> stream = fullData.stream();
        // 执行过滤 (Filters)
        if (rule.getFilters() != null && !rule.getFilters().isEmpty()) {
            for (DataProcessRule.Filter filter : rule.getFilters()) {
                stream = stream.filter(row -> applyFilter(row, filter));
            }
        }
        List<Map<String, Object>> filteredData = stream.collect(Collectors.toList());
        // 根据模式分发
        if ("EXTRACT".equalsIgnoreCase(rule.getMode())) {
            return executeExtractMode(filteredData, rule.getDimensions());
        } else {
            return executeAggregateMode(filteredData, rule);
        }
    }

    /**
     * 模式A：明细提取模式 (例如：散点图，只需提取指定的X和Y字段，不做聚合)
     */
    private List<Map<String, Object>> executeExtractMode(List<Map<String, Object>> data, List<String> fieldsToExtract) {
        if (fieldsToExtract == null || fieldsToExtract.isEmpty()) return data;

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Map<String, Object> newRow = new LinkedHashMap<>();
            for (String field : fieldsToExtract) {
                newRow.put(field, row.get(field));
            }
            result.add(newRow);
        }
        return result;
    }

    /**
     * 模式B：聚合计算模式 (例如：柱状图、饼图)
     * 注：这里为了演示代码清晰，假设只对第一个Dimension进行分组，对第一个Metric进行计算。
     */
    private List<Map<String, Object>> executeAggregateMode(List<Map<String, Object>> data, DataProcessRule rule) {
        if (rule.getDimensions() == null || rule.getDimensions().isEmpty()) return data;
        if (rule.getMetrics() == null || rule.getMetrics().isEmpty()) return data;

        String groupField = rule.getDimensions().get(0);
        DataProcessRule.Metric metric = rule.getMetrics().get(0);

        Map<String, Double> aggregatedMap = new LinkedHashMap<>();

        for (Map<String, Object> row : data) {
            String groupKey = String.valueOf(row.getOrDefault(groupField, "未知"));

            double value = 0.0;
            if (!"COUNT".equalsIgnoreCase(metric.operation) && row.get(metric.field) != null) {
                try {
                    value = Double.parseDouble(String.valueOf(row.get(metric.field)));
                } catch (NumberFormatException ignored) {}
            }

            if ("COUNT".equalsIgnoreCase(metric.operation)) {
                aggregatedMap.put(groupKey, aggregatedMap.getOrDefault(groupKey, 0.0) + 1.0);
            } else {
                aggregatedMap.put(groupKey, aggregatedMap.getOrDefault(groupKey, 0.0) + value);
            }
        }
        // 转回 List<Map> 格式返回
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : aggregatedMap.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put(groupField, entry.getKey());
            row.put(metric.field != null ? metric.field : "count", entry.getValue());
            resultList.add(row);
        }
        return resultList;
    }

    // 辅助方法：处理过滤逻辑
    // 升级后的过滤逻辑
    private boolean applyFilter(Map<String, Object> row, DataProcessRule.Filter filter) {
        if (filter.field == null || filter.value == null || filter.value.isEmpty()) return true;
        // 获取单元格真实值（可能是数字，也可能是字符串）
        String cellValue = String.valueOf(row.getOrDefault(filter.field, "")).trim();
        if (cellValue.isEmpty() || "null".equals(cellValue)) return false;
        String operator = filter.operator != null ? filter.operator.toUpperCase() : "CONTAINS";
        try {
            return switch (operator) {
                // 1. 精确匹配
                case "EQUALS", "EQ" -> cellValue.equals(filter.value);
                // 2. 模糊匹配
                case "CONTAINS", "LIKE" -> cellValue.contains(filter.value);
                // 3. 区间匹配 (核心修复：支持 2026-04-01,2026-04-30 这种格式)
                case "BETWEEN" -> {
                    String[] parts = filter.value.split(",");
                    if (parts.length == 2) {
                        String start = parts[0].trim();
                        String end = parts[1].trim();
                        // 字符串的 compareTo 可以完美处理 "YYYY-MM-DD" 格式的日期比较
                        yield cellValue.compareTo(start) >= 0 && cellValue.compareTo(end) <= 0;
                    }
                    yield false;
                }
                // 4. 数字大小比较 (考虑到业务中可能有 "> 1000" 的需求)
                case "GREATER_THAN", "GT" -> Double.parseDouble(cellValue) > Double.parseDouble(filter.value);
                case "LESS_THAN", "LT" -> Double.parseDouble(cellValue) < Double.parseDouble(filter.value);
                // 默认降级为包含
                default -> cellValue.contains(filter.value);
            };
        } catch (Exception e) {
            log.error("[引擎警告] 数据过滤比较失败: {}", e.getMessage());
            return false;
        }
    }
}