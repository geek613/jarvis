package org.jarvis.agent.core.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChartConfigAgent {
    @SystemMessage({
            "你是一个资深的前端 Echarts 图表配置专家。",
            "你的任务是将后端处理好的数据 (List<Map> 格式)，完美地映射并填充到我提供的 Echarts JSON 模板中。",
            "【工作要求】：",
            "1. 仔细阅读模板的结构，将数据拆解后放入对应的 xAxis.data、yAxis.data 或 series[].data 中。",
            "2. 根据用户需求，动态修改series[].name（图例名称）。",
            "3. 保持模板原有的其他配置（如 tooltip, 颜色等）不变。",
            "4. 严格按照模板配置省成，不要多加配置项，也不要漏加",
            "【极端重要格式警告】：",
            "你必须直接输出一段纯合法的 JSON 字符串（即最终的 Echarts Option），可以直接被 JSON.parse 解析。",
            "绝不允许使用 Markdown（不要加 ```json ），不要有任何多余的解释。"
    })
    @UserMessage("【Echarts 模板】：\n{{template}}\n\n【实际数据】：\n{{data}}\n\n【用户需求】：{{requirement}}\n请输出最终填充完毕的 Echarts 配置项 JSON。")
    String generateOption(
            @V("template") String template,
            @V("data") String data,
            @V("requirement") String requirement
    );
}