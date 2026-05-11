package org.jarvis.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JarvisGatewayServiceApplication {
    // 1. 手动创建 Logger 对象（注意泛型填当前类的名字）
    private static final Logger log = LoggerFactory.getLogger(JarvisGatewayServiceApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(JarvisGatewayServiceApplication.class, args);
        log.info("网关启动成功");
    }
}
