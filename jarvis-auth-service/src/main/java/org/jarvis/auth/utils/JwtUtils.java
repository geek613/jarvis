package org.jarvis.auth.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 JWT Token
     * @param userId 用户的唯一ID
     * @param username 用户名
     * @return 生成的 JWT 字符串
     */
    public String generateToken(String userId, String username) {
        // 1. 转换秘钥 (将字符串转为 JJWT 需要的 Key 对象)
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // 2. 计算过期时间
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        // 3. 构建 Token
        return Jwts.builder()
                .setHeaderParam("typ", "JWT") // 头部
                .setSubject(userId)           // 存入业务数据（通常把主体设为 UserID）
                .claim("username", username)  // 也可以存入额外的自定义信息
                .setIssuedAt(now)             // 签发时间
                .setExpiration(expireDate)    // 过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 签名算法和秘钥
                .compact();                   // 打包成最终的字符串
    }
}