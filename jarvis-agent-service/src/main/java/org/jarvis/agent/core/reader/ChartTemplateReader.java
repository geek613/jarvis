package org.jarvis.agent.core.reader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class ChartTemplateReader {

    public String readTemplate(String chartType) {
        String fileName = mapChartTypeToFileName(chartType);
        try {
            ClassPathResource resource = new ClassPathResource("chartType/" + fileName);
            System.out.println("成功读取图表模板: " + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("读取图表模板失败: " + fileName + ", " + e.getMessage());
            return "{}"; // 如果找不到模板，返回空 JSON
        }
    }

    // 简单匹配映射，大模型传什么词都能兜住
    private String mapChartTypeToFileName(String chartType) {
        if (chartType == null) return "bar.json";
        if (chartType.contains("柱") || chartType.toLowerCase().contains("bar")) return "bar.json";
        if (chartType.contains("散点") || chartType.toLowerCase().contains("scatter")) return "scatter.json";
        if (chartType.contains("折线") || chartType.toLowerCase().contains("line")) return "line.json";
        if (chartType.contains("饼") || chartType.toLowerCase().contains("pie")) return "pie.json";
        return "bar.json"; // 默认柱状图
    }
}