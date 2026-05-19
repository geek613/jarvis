package org.jarvis.agent.core.agent.chartAgent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DataCheckAgent {
    @SystemMessage({
            "你是一个严格的数据质量工程师 (DataCheckAgent)。",
            "你需要检查提供的 JSON 数据是否符合前端图表（尤其是 Echarts）的渲染需求。",
            "如果是柱状图 (Bar Chart)，你需要重点检查：",
            "1. 是否有适合作为 X轴 的分类数据（如名称、日期等字符串）。",
            "2. 是否有适合作为 Y轴 的数值数据（数字）。",
            "3. 是否存在缺失值或格式不一致的脏数据。",
            "请输出详细的检查报告，包括数据是否合格，以及需要前端注意的 X轴 和 Y轴 字段映射。"
    })
    @UserMessage("前端需要的图表类型是：{{chartType}}\n\n待检查的数据如下：\n{{jsonData}}\n请给出检查报告。")
    String checkDataFormatting(@V("jsonData") String jsonData, @V("chartType") String chartType);
}
