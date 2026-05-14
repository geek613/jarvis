package org.jarvis.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "security.ignore")
public class SecurityProperties {
    // 允许放行的 URL 列表
    private List<String> urls = new ArrayList<>();

    public List<String> getUrls() { return urls; }
    public void setUrls(List<String> urls) { this.urls = urls; }
}
