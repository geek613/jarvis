package org.jarvis.agent.core.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.agent.chat.domain.dto.JChatDto;
import org.jarvis.agent.core.agent.chartAgent.ChartConfigAgent;
import org.jarvis.agent.core.agent.chartAgent.DataCheckAgent;
import org.jarvis.agent.core.agent.chartAgent.DataProcessAgent;
import org.jarvis.agent.core.agent.chartAgent.LeaderAgent;
import org.jarvis.agent.core.domain.dto.ReviewAnalysisRequest;
import org.jarvis.agent.core.domain.dto.ReviewAnalysisResult;
import org.jarvis.agent.core.engine.DataProcessRuleEngine;
import org.jarvis.agent.core.reader.ChartTemplateReader;
import org.jarvis.agent.core.reader.ExcelReader;
import org.jarvis.agent.core.result.LeaderResult;
import org.jarvis.agent.core.tools.LeaderTools;
import org.jarvis.agent.factory.AiServiceFactory;
import org.jarvis.agent.service.ReviewAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/agent")
public class JarvisAgentController {
    @Autowired
    private AiServiceFactory aiServiceFactory;

    @Autowired
    private Gson gson;

    @Autowired
    private ChartTemplateReader templateReader;

    @Autowired
    private ExcelReader excelReader;

    @Autowired
    private DataProcessRuleEngine ruleEngine;

    @Autowired
    private ReviewAnalysisService reviewAnalysisService;
    @GetMapping("/generateChart")
    public JarvisResult<LeaderResult> generateChart(JChatDto chat) {
        long userId = chat.getUserId();
        ChartConfigAgent chartConfigAgent = aiServiceFactory.createService(ChartConfigAgent.class, false, userId);
        DataProcessAgent dataProcessAgent = aiServiceFactory.createService(DataProcessAgent.class, false, userId);
        DataCheckAgent dataCheckAgent = aiServiceFactory.createService(DataCheckAgent.class, false, userId);
        LeaderTools leaderTools = new LeaderTools(
                chartConfigAgent,
                dataProcessAgent,
                dataCheckAgent,
                templateReader, excelReader, ruleEngine, gson);
        LeaderAgent leaderAgent = aiServiceFactory.createService(LeaderAgent.class, false, userId, leaderTools);
        return JarvisResult.success(leaderAgent.handleTask(chat.getMessage()));
    }

    @PostMapping("/analyzeReviews")
    public JarvisResult<ReviewAnalysisResult> analyzeReviews(@RequestBody ReviewAnalysisRequest request) {
        long userId = request.getUserId();
        log.info("用户 {} 请求评论分析: fileUrl={}, topN={}", userId, request.getFileUrl(), request.getTopN());

        int topN = request.getTopN() != null && request.getTopN() > 0 ? request.getTopN() : 30;
        ReviewAnalysisResult result = reviewAnalysisService.analyze(request.getFileUrl(), topN, userId);

        return JarvisResult.success(result);
    }
}
