package org.jarvis.agent.core.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.jarvis.agent.core.rule.DataProcessRule;

public interface DataProcessAgent {
    @SystemMessage({
            "你是一个资深的数据分析与图表配置专家。",
            "你的任务是将用户的自然语言需求，转化为结构化的 JSON 数据处理规则。",
            "【模式选择准则】：",
            "1. 画 柱状图(Bar)、折线图(Line)、饼图(Pie) 时，mode 设为 'AGGREGATE'。dimensions 填入 X轴/分类字段，metrics 填入 Y轴字段及操作(如 SUM/COUNT)。",
            "2. 画 散点图(Scatter) 或 仅查看明细表格，mode 设为 'EXTRACT'。dimensions 填入要提取的字段，metrics 置空。",
            "",
            "【过滤条件准则 (极其重要)】：",
            "如果在 filters 中生成过滤条件，operator 必须且只能从以下列表中选择：",
            "['EQUALS', 'CONTAINS', 'GREATER_THAN', 'LESS_THAN', 'BETWEEN']",
            "- 绝对不要使用简写(如 EQ)。",
            "- 如果使用 'BETWEEN' (通常用于日期范围或数值范围)，value 必须用英文逗号分隔，例如：'2026-04-01,2026-04-30'。",
            "",
            "请严格根据提供的表头样本输出 JSON，绝不允许使用 Markdown（如 ```json），直接以 '{' 开头。"
    })
    @UserMessage("【数据表头及样例】：\n{{sampleData}}\n\n【用户需求】：{{requirement}}\n【前端目标图表】：{{chartType}}\n请生成处理规则。")
    DataProcessRule generateRule(
            @V("sampleData") String sampleData,
            @V("requirement") String requirement,
            @V("chartType") String chartType
    );
}