package org.jarvis.agent.core.tools;

import com.google.gson.Gson;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jarvis.agent.core.agent.DataCheckAgent;
import org.jarvis.agent.core.agent.DataProcessAgent;
import org.jarvis.agent.core.engine.DataProcessRuleEngine;
import org.jarvis.agent.core.rule.DataProcessRule;
import org.jarvis.agent.utils.ExcelReaderUtils;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LeaderTools {

    private final ExcelReaderUtils excelReader;
    private final DataProcessAgent dataProcessAgent;
    private final DataProcessRuleEngine ruleEngine;
    private final DataCheckAgent dataCheckAgent;
    private final Gson gson = new Gson();

    public LeaderTools(ExcelReaderUtils excelReader, DataProcessAgent dataProcessAgent, DataProcessRuleEngine ruleEngine, DataCheckAgent dataCheckAgent) {
        this.excelReader = excelReader;
        this.dataProcessAgent = dataProcessAgent;
        this.ruleEngine = ruleEngine;
        this.dataCheckAgent = dataCheckAgent;
    }

    @Tool("步骤1：处理 Excel 数据。输入参数为文件路径(filePath)、用户需求(requirement) 和 前端图表类型(chartType)。它会自动提取样本请求AI生成规则，并在底层聚合海量数据，最终返回聚合后的 JSON 字符串。")
    public String processAndAggregateData(
            @P("Excel文件的绝对路径") String filePath,
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
}