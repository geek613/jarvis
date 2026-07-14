package org.jarvis.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secret;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取 Token
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        // 如果 Token 存在，进行解析验证
        if (StringUtils.hasText(token)) {
            authenticateFromToken(token);
        } else {
            // 检查是否来自 Gateway 的 user-id 头（Gateway 已验证过 Token）
            String gatewayUserId = request.getHeader("user-id");
            if (StringUtils.hasText(gatewayUserId)) {
                authenticateFromUserId(gatewayUserId);
            } else {
                // Token 和 user-id 都不存在，清除上下文
                SecurityContextHolder.clearContext();
            }
        }
        // 放行请求，交给下一个过滤器或 Controller
        filterChain.doFilter(request, response);
    }

    private void authenticateFromToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.getSubject();
            authenticateFromUserId(userId);
        } catch (Exception e) {
            logger.error("Token 解析失败: " + e.getMessage());
        }
    }

    private void authenticateFromUserId(String userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
