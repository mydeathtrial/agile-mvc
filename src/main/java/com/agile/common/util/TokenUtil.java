package com.agile.common.util;

import com.agile.common.properties.SecurityProperties;
import com.agile.common.security.LoginTokenInfo;
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
 * @author 佟盟 on 2018/7/4
 */
public class TokenUtil {
    /**
     * 存储账号信息的缓存key
     */
    public static final String AUTHENTICATION_CACHE_KEY = "AUTHENTICATION_CACHE_KEY";
    /**
     * 存储当前会话口令
     */
    public static final String AUTHENTICATION_CACHE_SALT_KEY = "AUTHENTICATION_CACHE_SALT_KEY";
    /**
     * 密码
     */
    public static final String AUTHENTICATION_CREATE_SALT_VALUE = "AUTHENTICATION_CACHE_SALT_VALUE";

    public static final String AUTHENTICATION_CREATE_TIME = "created";

    public static final String NO_KEY = "NO_KEY";
    private static SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);

    private static final int SECOND = 1000;

    /**
     * 根据 TokenDetail 生成 Token
     */
    public static String generateToken(String cacheKey, String salt, String saltValue) {
        final int length = 4;
        Map<String, Object> claims = new HashMap<>(length);
        claims.put(AUTHENTICATION_CACHE_KEY, cacheKey);
        claims.put(AUTHENTICATION_CACHE_SALT_KEY, salt);
        claims.put(AUTHENTICATION_CREATE_SALT_VALUE, saltValue);
        claims.put(AUTHENTICATION_CREATE_TIME, DateUtil.getCurrentDate());
        return generateToken(claims);
    }

    /**
     * 根据 TokenDetail 生成 Token
     */
    public static String generateToken(String salt, String saltValue) {
        return generateToken(NO_KEY, salt, saltValue);
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
                .signWith(SignatureAlgorithm.HS512, securityProperties.getTokenKey().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * token 过期时间
     */
    public static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + securityProperties.getTokenTimeout() * SECOND);
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
                    .setSigningKey(securityProperties.getTokenKey().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static LoginTokenInfo initLoginTokenInfo(String token) {
        return new LoginTokenInfo(token);
    }

    public static boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (ObjectUtil.isEmpty(claims)) {
            return false;
        }
        return claims.getExpiration().after(DateUtil.getCurrentDate());
    }

    public static boolean isToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return !ObjectUtil.isEmpty(claims);
    }
}
