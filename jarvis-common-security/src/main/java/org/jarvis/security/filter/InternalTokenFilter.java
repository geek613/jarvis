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
            try {
                // TODO: 替换为JWT解析逻辑
                Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String userId = claims.getSubject();
                // 构建 Spring Security 的 Authentication 对象
                // 参数：用户信息(通常是 userId 或 UserDetail 对象), 凭证(可传null), 权限列表(RBAC)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                // 将认证信息存入上下文，表示该请求已通过认证
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 解析失败（过期、伪造等），不设置 SecurityContext，后续会被拦截
                logger.error("Token 解析失败: " + e.getMessage());
            }
        }
        // 放行请求，交给下一个过滤器或 Controller
        filterChain.doFilter(request, response);
    }
}
