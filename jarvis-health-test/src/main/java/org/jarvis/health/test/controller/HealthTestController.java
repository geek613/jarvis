package org.jarvis.health.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/test")
public class HealthTestController {
    @Value("${health-test.key:hello}")
    private String test;
    @GetMapping
    public String health() {
        return test;
    }
}
