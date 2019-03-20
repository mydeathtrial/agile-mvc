package com.agile.common.util;

import com.agile.common.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
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
    public static final String AUTHENTICATION_USER_NAME = "AUTHENTICATION_USER_NAME";
    /**
     * 存储当前会话口令
     */
    public static final String AUTHENTICATION_SESSION_TOKEN = "AUTHENTICATION_SESSION_TOKEN";

    private static final String AUTHENTICATION_CREATE_TIME = "created";

    private static SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);


    /**
     * 根据 TokenDetail 生成 Token
     */
    public static String generateToken(String username, Long sessionToken, Date timeout) {
        final int length = 4;
        Map<String, Object> claims = new HashMap<>(length);
        claims.put(AUTHENTICATION_USER_NAME, username);
        claims.put(AUTHENTICATION_SESSION_TOKEN, sessionToken);
        claims.put(AUTHENTICATION_CREATE_TIME, DateUtil.getCurrentDate());
        return generateToken(claims, timeout);
    }

    public static String generateToken(Map<String, Object> claims, Date timeout) {
        return Jwts.builder()
                .setIssuer("agile")
                .setIssuedAt(DateUtil.getCurrentDate())
                .setSubject("Login")
                .setAudience("System Users")
                .setNotBefore(DateUtil.getCurrentDate())
                .setId(Long.toString(IdUtil.generatorId()))
                .setClaims(claims)
                .setExpiration(timeout)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getTokenSecret().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 通知前端
     *
     * @param httpServletResponse 响应
     * @param token               令牌
     */
    public static void notice(HttpServletResponse httpServletResponse, String token) {
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), token);
    }

    /**
     * 解析 token 的主体 Claims
     */
    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(securityProperties.getTokenSecret().getBytes(StandardCharsets.UTF_8))
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
