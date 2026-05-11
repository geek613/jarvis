package org.jarvis.health.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HealthTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthTestApplication.class, args);
    }
}
