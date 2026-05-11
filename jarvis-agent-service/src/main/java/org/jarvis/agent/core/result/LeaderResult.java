package org.jarvis.agent.core.result;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LeaderResult {
    private boolean isPass; // 数据检查是否通过
    private String summary; // 给前端/用户的总结和检查报告
    private List<Map<String, Object>> chartData; // 最终给前端 Echarts 渲染用的数据
}
