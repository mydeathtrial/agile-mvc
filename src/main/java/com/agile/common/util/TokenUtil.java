package com.agile.common.util;

import com.agile.common.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/7/4
 */
public class TokenUtil {
    public static final String AUTHENTICATION_CACHE_KEY = "AUTHENTICATION_CACHE_KEY";
    public static final String AUTHENTICATION_CACHE_SALT_KEY = "AUTHENTICATION_CACHE_SALT_KEY";
    public static final String AUTHENTICATION_CREATE_SALT_VALUE = "AUTHENTICATION_CACHE_SALT_VALUE";
    public static final String AUTHENTICATION_CREATE_TIME = "created";

    /**
     * 根据 TokenDetail 生成 Token
     */
    public static String generateToken(String cacheKey, String salt, String saltValue) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHENTICATION_CACHE_KEY, cacheKey);
        claims.put(AUTHENTICATION_CACHE_SALT_KEY, salt);
        claims.put(AUTHENTICATION_CREATE_SALT_VALUE, saltValue);
        claims.put(AUTHENTICATION_CREATE_TIME, DateUtil.getCurrentDate());
        return generateToken(claims);
    }

    public static String getToken(HttpServletRequest request, String key) {
        String token = request.getHeader(key);
        if (StringUtil.isBlank(token)) {
            try {
                Cookie cook = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(key)).findFirst().get();
                token = cook.getValue();
            } catch (Exception e) {
                token = null;
            }
        }
        return token;
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SecurityProperties.getTokenKey().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * token 过期时间
     */
    public static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + SecurityProperties.getTokenTimeout() * 1000);
    }

    /**
     * 从 token 中拿到 username
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 解析 token 的主体 Claims
     */
    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SecurityProperties.getTokenKey().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (ObjectUtil.isEmpty(claims)) {
            return false;
        }
        return claims.getExpiration().after(DateUtil.getCurrentDate());
    }
}
