package org.jarvis.agent.core.agent.commentAgent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReviewHotwordAgent {
    @SystemMessage({
            "你是电商评论分析专家。给你一批用户评论文本，请提取反映产品优缺点/用户态度的高频关键词或短语（2-4字词组优先，如'质量好''发货慢''包装精美'），",
            "统计其在本批次内的出现频次。只返回JSON数组，不要输出其他任何文字：",
            "[{\"word\":\"xxx\",\"count\":n}]"
    })
    @UserMessage("请分析以下{{count}}条评论：\n{{reviews}}")
    String extractHotwords(@V("count") int count, @V("reviews") String reviews);
}
