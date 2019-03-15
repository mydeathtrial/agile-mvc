package com.agile.common.security;

import com.agile.common.base.Constant;
import com.agile.common.cache.Cache;
import com.agile.common.exception.TokenIllegalException;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author 佟盟
 * 日期 2019/3/15 14:10
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Setter
@Getter
public class LoginTokenInfo {
    private String token;
    private String sessionPassword;
    private String sessionPasswordCacheKey;
    private Set<String> sessionPasswordCacheValue = new HashSet<>();
    private String userInfoCacheKey;
    private Authentication authentication;

    private String saltValue;

    private Cache cache = CacheUtil.getCache(Objects.requireNonNull(FactoryUtil.getBean(SecurityProperties.class)).getTokenHeader());

    public LoginTokenInfo(String token) {
        this.token = token;

        Claims claims = TokenUtil.getClaimsFromToken(token);
        if (claims == null) {
            throw new TokenIllegalException("身份令牌验证失败");
        }
        saltValue = claims.get(TokenUtil.AUTHENTICATION_CREATE_SALT_VALUE, String.class);
        sessionPassword = claims.get(TokenUtil.AUTHENTICATION_CACHE_SALT_KEY, String.class);
        userInfoCacheKey = claims.get(TokenUtil.AUTHENTICATION_CACHE_KEY, String.class);

        if (TokenUtil.NO_KEY.equals(userInfoCacheKey)) {
            authentication = cache.get(token, Authentication.class);
        } else {
            authentication = cache.get(userInfoCacheKey, Authentication.class);
        }

        sessionPasswordCacheKey = String.format("%s_SALT", userInfoCacheKey);
        String sessionPasswordCacheValueStr = cache.get(sessionPasswordCacheKey, String.class);
        if (StringUtil.isNotBlank(sessionPasswordCacheValueStr)) {
            String[] s = sessionPasswordCacheValueStr.split("[,]");
            sessionPasswordCacheValue.addAll(Arrays.asList(s));
        }

    }

    public String refreshSessionPassword(String newSessionPassword) {
        sessionPasswordCacheValue.remove(sessionPassword);
        if (StringUtil.isNotBlank(newSessionPassword)) {
            sessionPasswordCacheValue.add(newSessionPassword);
        }
        return StringUtil.join(sessionPasswordCacheValue.toArray(), Constant.RegularAbout.COMMA);
    }

}
