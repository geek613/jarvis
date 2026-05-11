package org.jarvis.agent.core.configuration;

import org.jarvis.agent.core.agent.DataCheckAgent;
import org.jarvis.agent.core.agent.DataProcessAgent;
import org.jarvis.agent.core.agent.LeaderAgent;
import org.jarvis.agent.core.tools.LeaderTools;
import org.jarvis.agent.factory.AiServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {
    @Autowired
    private AiServiceFactory aiServiceFactory;
    @Bean
    public DataProcessAgent dataProcessAgent() {
        return aiServiceFactory.createService(DataProcessAgent.class);
    }
    @Bean
    public DataCheckAgent dataCheckAgent() {
        return aiServiceFactory.createService(DataCheckAgent.class);
    }
    @Bean
    public LeaderAgent leaderAgent(LeaderTools leaderTools) {
        return aiServiceFactory.createService(LeaderAgent.class, leaderTools);
    }
}