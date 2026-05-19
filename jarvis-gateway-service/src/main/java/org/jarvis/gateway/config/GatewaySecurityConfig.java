package org.jarvis.gateway.config;

import org.jarvis.gateway.point.CustomAccessDeniedHandler;
import org.jarvis.gateway.point.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 1. 禁用 CSRF，因为网关通常是前后端分离的无状态 API
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        // 2. 配置请求路径的权限规则
        http.authorizeExchange(exchange -> exchange
                // 允许跨域的 OPTIONS 请求
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                // 白名单：登录、获取 Token、Swagger 等接口放行
                .pathMatchers("/api/auth/**", "/v3/api-docs/**","/api/system/login","/api/system/register").permitAll()
                // 静态资源放行
                .pathMatchers("/favicon.ico").permitAll()
                // 其他所有请求必须认证
                .anyExchange().authenticated()
        );

        // 3. 配置 OAuth2 资源服务器，使用 JWT 认证
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                // 4. 定制 401 (未登录) 和 403 (无权限) 的 JSON 响应 (见下面第三步)
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        return http.build();
    }

    /**
     * 提取 JWT 中的权限字段 (例如从 payload 的 "authorities" 字段提取角色)
     * 默认情况下，Spring Security 会找 "scope" 或 "scp" 字段。
     */
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 将 JWT 中的 roles 字段映射为权限，加上 ROLE_ 前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}