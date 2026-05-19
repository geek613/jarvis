package org.jarvis.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @Order(-1) 的目的是让它的优先级高于 Spring Boot 默认的 DefaultErrorWebExceptionHandler
 */
@Configuration
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 如果响应已经 committed（比如已经开始往客户端写数据了），就直接放过，避免冲突
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        // 1. 确定异常类型与状态码
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "网关内部服务器错误";

        if (ex instanceof ResponseStatusException) {
            // 处理 Spring 底层抛出的状态异常 (适用于 Spring Boot 3.x)
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;

            // 1. 使用 getStatusCode().value() 获取 int 类型的状态码，再转回 HttpStatus
            int statusCodeValue = responseStatusException.getStatusCode().value();
            status = HttpStatus.resolve(statusCodeValue);
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR; // 兜底保护
            }

            // 2. 使用 getReason() 获取干净的错误信息 (例如只返回 "Not Found")
            // 之前的 getMessage() 会返回类似 "404 NOT_FOUND 'No matching handler'" 这种带前缀的丑陋字符串
            errorMessage = responseStatusException.getReason();
            if (errorMessage == null) {
                errorMessage = "请求错误";
            }
        } else if (ex instanceof java.net.ConnectException) {
            // 处理下游微服务宕机或拒绝连接的情况 (极其常见)
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorMessage = "下游微服务暂时不可用，请稍后再试";
        } else {
            // 记录未知的异常堆栈
            log.error("网关拦截到全局异常 [{}]", exchange.getRequest().getPath(), ex);
        }

        // 2. 构建返回给前端的统一 JSON 结构 (这里可以替换为你自己的 JarvisResult)
        Map<String, Object> result = new HashMap<>();
        result.put("code", status.value());
        result.put("message", errorMessage);
        // 不建议在生产环境返回具体的异常 detail 字符串
        // result.put("detail", ex.getMessage());

        // 3. 将 Map 转换为 JSON 字节流，并写入响应
        return writeResponse(exchange, status, result);
    }

    /**
     * 构建非阻塞的 Response
     */
    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, Map<String, Object> result) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            try {
                // 将 JSON 对象序列化为字节数组
                byte[] errorResponse = objectMapper.writeValueAsBytes(result);
                return bufferFactory.wrap(errorResponse);
            } catch (JsonProcessingException e) {
                log.error("JSON 序列化失败", e);
                return bufferFactory.wrap("{\"code\":500,\"message\":\"系统内部严重错误\"}".getBytes());
            }
        }));
    }
}