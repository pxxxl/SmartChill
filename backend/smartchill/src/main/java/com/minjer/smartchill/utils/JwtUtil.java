package com.minjer.smartchill.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {
    // 秘钥
    private static final String SECRET_KEY = "962762007e3934d77a52f";

    // 令牌过期时间(1天)
    private static final long EXPIRATION_TIME = 864_000_00;
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // 生成 JWT 令牌
    public static String generateToken(String username) {
        String jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        log.info("generateToken: " + jwt);
        return jwt;
    }

    // 解析 JWT 令牌
    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // 从 JWT 令牌中获取用户名
    public static String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 验证令牌是否过期
    public static boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
