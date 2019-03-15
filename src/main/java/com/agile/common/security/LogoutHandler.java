package com.agile.common.security;

import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.cache.Cache;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import com.agile.common.util.ViewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/7/6
 */
public class LogoutHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private CustomerUserDetailsService securityUserDetailsService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        clearToken(request, response);

        try {
            assert RETURN.LOGOUT_SUCCESS != null;
            ViewUtil.render(new Head(RETURN.LOGOUT_SUCCESS), null, request, response);
        } catch (Exception e) {
            LoggerFactory.AUTHORITY_LOG.debug(e);
        }
    }

    private void clearToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String token = TokenUtil.getToken(httpServletRequest, securityProperties.getTokenHeader());
        Cache tokenCache = CacheUtil.getCache(securityProperties.getTokenHeader());

        //判断策略
        if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
            difficultTokenClear(tokenCache, token);
        } else {
            easyTokenClear(tokenCache, token);
        }
        clearHeader(httpServletResponse);

        LoggerFactory.AUTHORITY_LOG.info(String.format("账号退出[token：%s]", token));
    }

    /**
     * 简单模式token清理
     */
    private void easyTokenClear(Cache tokenCache, String token) {
        LoginTokenInfo loginTokenInfo = TokenUtil.initLoginTokenInfo(token);
        CustomerUserDetails userDetails = (CustomerUserDetails) loginTokenInfo.getAuthentication().getDetails();
        securityUserDetailsService.stopLoginInfo(userDetails.getUsername(), token);
        tokenCache.evict(token);
    }

    /**
     * 复杂模式token清理
     */
    private void difficultTokenClear(Cache tokenCache, String token) {

        LoginTokenInfo loginTokenInfo = TokenUtil.initLoginTokenInfo(token);
        CustomerUserDetails userDetails = (CustomerUserDetails) loginTokenInfo.getAuthentication().getDetails();

        securityUserDetailsService.stopLoginInfo(userDetails.getUsername(), loginTokenInfo.getSessionPassword());
        String set = loginTokenInfo.refreshSessionPassword(null);
        if (StringUtil.isBlank(set)) {
            tokenCache.evict(loginTokenInfo.getSessionPasswordCacheKey());
        } else {
            tokenCache.put(loginTokenInfo.getSessionPasswordCacheKey(), set);
        }
    }

    /**
     * 清空头部信息
     *
     * @param httpServletResponse 响应
     */
    private void clearHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), Constant.RegularAbout.BLANK);
    }

    /**
     * 清空cookies
     */
    private void cleanCookie() {
//        Cookie cookie = new Cookie(securityProperties.getTokenHeader(), null);
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(0);
//        cookie.setPath(Constant.RegularAbout.SLASH);
//        httpServletResponse.addCookie(cookie);
    }
}
