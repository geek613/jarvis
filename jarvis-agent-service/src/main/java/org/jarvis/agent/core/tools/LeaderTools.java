package org.jarvis.agent.core.tools;

import com.google.gson.Gson;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jarvis.agent.core.agent.ChartConfigAgent;
import org.jarvis.agent.core.agent.DataCheckAgent;
import org.jarvis.agent.core.agent.DataProcessAgent;
import org.jarvis.agent.core.engine.DataProcessRuleEngine;
import org.jarvis.agent.core.reader.ChartTemplateReader;
import org.jarvis.agent.core.rule.DataProcessRule;
import org.jarvis.agent.core.reader.ExcelReader;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LeaderTools {
    private final ChartTemplateReader templateReader;
    private final ChartConfigAgent chartConfigAgent;
    private final ExcelReader excelReader;
    private final DataProcessAgent dataProcessAgent;
    private final DataProcessRuleEngine ruleEngine;
    private final DataCheckAgent dataCheckAgent;
    private final Gson gson = new Gson();

    public LeaderTools(ChartTemplateReader templateReader, ChartConfigAgent chartConfigAgent, ExcelReader excelReader, DataProcessAgent dataProcessAgent, DataProcessRuleEngine ruleEngine, DataCheckAgent dataCheckAgent) {
        this.templateReader = templateReader;
        this.chartConfigAgent = chartConfigAgent;
        this.excelReader = excelReader;
        this.dataProcessAgent = dataProcessAgent;
        this.ruleEngine = ruleEngine;
        this.dataCheckAgent = dataCheckAgent;
    }

    @Tool("步骤1：处理 Excel 数据。输入参数为文件路径(filePath)、用户需求(requirement) 和 前端图表类型(chartType)。它会自动提取样本请求AI生成规则，并在底层聚合海量数据，最终返回聚合后的 JSON 字符串。")
    public String processAndAggregateData(
            @P("Excel文件的本地绝对路径 或 包含http/https的云端网络URL") String filePath,
            @P("用户的具体数据提取和聚合需求") String requirement,
            @P("前端需要的图表类型，如'柱状图'") String chartType) {
        log.info("[系统] 开始执行流式处理，目标图表: {}", chartType);
        // 读取全量数据
        List<Map<String, Object>> fullData = excelReader.readExcelAsList(filePath);
        if (fullData.isEmpty()) return "[]";
        // 截取前3行作为探针样本
        List<Map<String, Object>> sampleData = fullData.stream().limit(3).toList();
        String sampleJson = gson.toJson(sampleData);
        // Agent生成规则
        log.info("[系统] 正在呼叫 DataProcessAgent 生成动态规则...");
        DataProcessRule rule = dataProcessAgent.generateRule(sampleJson, requirement, chartType);
        log.info("[系统] 大模型生成的规则: {}", gson.toJson(rule));
        // 引擎执行计算
        log.info("[系统] Java 引擎开始执行全量数据清洗/聚合...");
        List<Map<String, Object>> resultData = ruleEngine.execute(fullData, rule);
        return gson.toJson(resultData);
    }

    @Tool("步骤2：委托 DataCheckAgent 检查最终数据格式。输入 processedJsonData 是步骤1处理后的数据，chartType 是前端图表类型。返回检查报告。")
    public String delegateToDataCheckAgent(
            @P("步骤1处理后返回的 JSON 字符串") String processedJsonData,
            @P("前端图表类型，如'柱状图'") String chartType) {
        return dataCheckAgent.checkDataFormatting(processedJsonData, chartType);
    }

    @Tool("步骤3：生成 Echarts 配置项。输入参数为步骤1处理后的数据、图表类型、以及用户的原始需求。它会读取本地模板，将数据填充为前端可直接使用的 Echarts Option JSON。")
    public String generateEchartsOption(
            @P("步骤1处理完毕的 JSON 数据") String processedData,
            @P("图表类型(如柱状图)") String chartType,
            @P("用户的原始需求，用于生成标题") String requirement) {

        System.out.println("[系统] 正在读取图表模板: " + chartType);

        // 1. Java 负责读取模板文件
        String templateJson = templateReader.readTemplate(chartType);

        // 2. 召唤 ChartConfigAgent 进行数据填充
        log.info("[系统] 正在呼叫 ChartConfigAgent 渲染 Echarts 配置...");
        String finalEchartsOption = chartConfigAgent.generateOption(templateJson, processedData, requirement);

       log.info("[系统] Echarts 配置渲染完成！");
        return finalEchartsOption;
    }
}