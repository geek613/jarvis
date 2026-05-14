package org.jarvis.security.config;

import org.jarvis.security.filter.InternalTokenFilter;
import org.jarvis.security.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// 开启方法级权限控制，例如可以使用 @PreAuthorize 注解
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private InternalTokenFilter internalTokenFilter;
    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 把 List 转换成数组
        String[] ignoreUrls = securityProperties.getUrls().toArray(new String[0]);
        http
                // 1. 微服务通常是无状态的，关闭 CSRF 保护
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 基于 Token，不需要 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置路由权限规则
                .authorizeHttpRequests(auth -> {
                    // 如果配置了白名单，则动态放行
                    if (ignoreUrls.length > 0) {
                        auth.requestMatchers(ignoreUrls).permitAll();
                    }
                    auth
                            // 开发公共接口（如健康检查、部分不需要登录的接口）
                            .requestMatchers("/actuator/**", "/public/**").permitAll()
                            // 其他所有请求都必须认证
                            .anyRequest().authenticated();
                })
                // 4. 把我们自定义的 Token 过滤器加到默认的用户名密码过滤器之前
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
