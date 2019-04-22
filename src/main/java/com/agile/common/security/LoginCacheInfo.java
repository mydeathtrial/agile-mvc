package com.agile.common.security;

import com.agile.common.base.Constant;
import com.agile.common.exception.NoSignInException;
import com.agile.common.exception.TokenIllegalException;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 佟盟
 * 日期 2019/3/20 19:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
class LoginCacheInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Authentication authentication;
    private Map<Long, TokenInfo> sessionTokens;

    private static Cache cache = CacheUtil.getCache(Objects.requireNonNull(FactoryUtil.getBean(SecurityProperties.class)).getTokenHeader());

    /**
     * 创建登陆信息
     *
     * @param username       账号
     * @param authentication 用户权限信息
     * @param sessionToken   本次会话令牌
     * @param token          信息令牌
     * @param start          开始时间
     * @param end            结束时间
     * @return
     */
    static LoginCacheInfo createLoginCacheInfo(String username, Authentication authentication, Long sessionToken, String token, Date start, Date end) {
        LoginCacheInfo loginCacheInfo = cache.get(username, LoginCacheInfo.class);
        Map<Long, TokenInfo> sessionTokens;

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setStart(start);
        tokenInfo.setEnd(end);

        if (loginCacheInfo == null) {
            loginCacheInfo = new LoginCacheInfo();
            loginCacheInfo.setUsername(username);
            loginCacheInfo.setAuthentication(authentication);
            sessionTokens = new HashMap<>(Constant.NumberAbout.ONE);
        } else {
            sessionTokens = loginCacheInfo.getSessionTokens();
            parsingTimeOut(sessionTokens);
        }
        sessionTokens.put(sessionToken, tokenInfo);
        loginCacheInfo.setSessionTokens(sessionTokens);
        return loginCacheInfo;
    }

    /**
     * 处理过期数据
     *
     * @param sessionTokens 会话令牌集合
     */
    private static void parsingTimeOut(Map<Long, TokenInfo> sessionTokens) {
        for (Map.Entry<Long, TokenInfo> entity : sessionTokens.entrySet()) {
            Long key = entity.getKey();
            TokenInfo tokenInfo = entity.getValue();
            if (DateUtil.compareDate(tokenInfo.getEnd(), tokenInfo.getStart()) <= 0) {
                sessionTokens.remove(key);
            }
        }
    }

    /**
     * 根据token令牌获取用户缓存信息
     *
     * @param token 令牌
     * @return 用户缓存信息
     */
    static CurrentLoginInfo getCurrentLoginInfo(String token) {
        if (StringUtil.isBlank(token)) {
            throw new NoSignInException("账号尚未登陆");
        }

        if (!TokenUtil.validateToken(token)) {
            throw new TokenIllegalException("身份令牌已过期");
        }

        Claims claims = TokenUtil.getClaimsFromToken(token);
        if (claims == null) {
            throw new TokenIllegalException("身份令牌验证失败");
        }

        Long sessionToken = claims.get(TokenUtil.AUTHENTICATION_SESSION_TOKEN, Long.class);
        String username = claims.get(TokenUtil.AUTHENTICATION_USER_NAME, String.class);

        LoginCacheInfo loginCacheInfo = cache.get(username, LoginCacheInfo.class);

        if (loginCacheInfo == null || !loginCacheInfo.getSessionTokens().containsKey(sessionToken)) {
            throw new TokenIllegalException("身份令牌验证失败");
        }

        SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);

        //判断策略,复杂策略时，更新会话令牌
        if (securityProperties != null && securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
            //创建新会话令牌
            long newSessionToken = IdUtil.generatorId();

            //更新数据库登陆信息
            CustomerUserDetailsService securityUserDetailsService = FactoryUtil.getBean(CustomerUserDetailsService.class);
            assert securityUserDetailsService != null;
            securityUserDetailsService.updateLoginInfo(username, Long.toString(sessionToken), Long.toString(newSessionToken));

            //删除旧的缓存会话令牌
            loginCacheInfo.getSessionTokens().remove(sessionToken);

            //生成新的会话令牌缓存
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setToken(token);
            tokenInfo.setStart(new Date());
            tokenInfo.setEnd(DateUtil.add(securityProperties.getTokenTimeout()));

            loginCacheInfo.getSessionTokens().put(newSessionToken, tokenInfo);
            cache.put(username, loginCacheInfo);
        }

        return new CurrentLoginInfo(sessionToken, loginCacheInfo);
    }

    /**
     * 退出操作，根据token删除指定会话令牌
     *
     * @param token 令牌
     */
    public static void remove(String token) {
        remove(getCurrentLoginInfo(token));
    }

    /**
     * 退出操作，根据currentLoginInfo删除指定会话令牌
     *
     * @param currentLoginInfo 当前登陆信息
     */
    public static void remove(CurrentLoginInfo currentLoginInfo) {
        currentLoginInfo.getLoginCacheInfo().getSessionTokens().remove(currentLoginInfo.getSessionToken());
        if (currentLoginInfo.getLoginCacheInfo().getSessionTokens().size() > 0) {
            cache.put(currentLoginInfo.getLoginCacheInfo().getUsername(), currentLoginInfo.getLoginCacheInfo());
        } else {
            cache.evict(currentLoginInfo.getLoginCacheInfo().getUsername());
        }
    }
}
