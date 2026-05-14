package org.jarvis.health.test.controller;

import org.Jarvis.common.result.JarvisResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/health")
public class SystemHealthController {
    @Value("${health-test.key:hello}")
    private String test;
    @GetMapping
    public String health() {
        return test;
    }

    @GetMapping("/getUUid")
    public JarvisResult<String> getUUid() {
        return JarvisResult.success(UUID.randomUUID().toString());
    }

    @GetMapping("/getServerTime")
    public JarvisResult<Date> getServerTime() {
        return JarvisResult.success(new Date());
    }
}
