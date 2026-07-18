package org.jarvis.agent;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableFeignClients
public class JarvisAgentServiceApplication {
    @Autowired
    private Environment env;
    public static void main(String[] args) {
        SpringApplication.run(JarvisAgentServiceApplication.class, args);
    }
    @PostConstruct
    public void init() {
        System.out.println(">>> Nacos Server Addr: " + env.getProperty("spring.cloud.nacos.discovery.server-addr"));
    }
}
