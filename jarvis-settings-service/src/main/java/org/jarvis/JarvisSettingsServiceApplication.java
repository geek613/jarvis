package org.jarvis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class JarvisSettingsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JarvisSettingsServiceApplication.class, args);
    }
}