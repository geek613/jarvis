package org.jarvis.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 2. 如果是登录接口，直接放行
        String path = request.getURI().getPath();
        if (path.contains("/system/login")) {
            return chain.filter(exchange);
        }
        // 3. 获取 Token
        String authHeader = request.getHeaders().getFirst("Authorization");

        // 4. 判断 Token
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(response, "未登录或请求未携带有效的 Token");
        }
        String token = authHeader.substring(7); // 截取 "Bearer " 之后的内容

        try {
            // 5. 解析并校验 Token
            Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 6. 从 Claims 中提取 userId
            String userId = claims.getSubject();
            log.info("Token 校验通过，用户 ID: {}", userId);

            // 7. 将userId放入Header，给下游微服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("user-id", userId)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            // Token 过期或非法，拦截并返回 401
            return unauthorizedResponse(response, "Token已过期或不合法");
        }
    }
    /**
     * 自定义返回JSON格式的错误信息
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        // 1. 设置 HTTP 状态码为 401
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 2. 设置响应头的内容类型为 JSON，并指定 UTF-8 编码防乱码
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        // 3. 构建你要返回的 JSON 数据内容
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 401);
        resultMap.put("message", message);
        resultMap.put("data", null);

        // 4. 将 Map 转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(resultMap);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化异常", e);
        }
        // 5. 将字节数组包装成 WebFlux 需要的 DataBuffer
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        // 6. 写入响应体并结束请求
        return response.writeWith(Mono.just(buffer));
    }
    @Override
    public int getOrder() {
        // 过滤器的优先级，数字越小优先级越高
        return -1;
    }
}