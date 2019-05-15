package com.agile.common.security;

import com.agile.common.exception.TokenIllegalException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.TokenUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 登陆验证码拦截器
 *
 * @author 佟盟 on 2017/9/27
 */
public class TokenFilter extends OncePerRequestFilter {
    private final FailureHandler failureHandler;
    private List<RequestMatcher> matches;

    private SecurityProperties securityProperties;


    public TokenFilter(String[] immuneUrl, SecurityProperties securityProperties, FailureHandler failureHandler) {
        matches = ServletUtil.coverRequestMatcher(immuneUrl);
        this.failureHandler = failureHandler;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (ServletUtil.matcherRequest(request, matches)) {
                filterChain.doFilter(request, response);
                return;
            }
            //获取令牌
            String token = ServletUtil.getInfo(request, securityProperties.getTokenHeader());

            //获取当前登陆信息
            CurrentLoginInfo currentLoginInfo = LoginCacheInfo.getCurrentLoginInfo(token);

            //判断策略
            if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
                refreshToken(currentLoginInfo, response);
            }

            SecurityContextHolder.getContext().setAuthentication(currentLoginInfo.getLoginCacheInfo().getAuthentication());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                exceptionHandler(request, response, (AuthenticationException) e);
            } else {
                LoggerFactory.AUTHORITY_LOG.error(e);
                exceptionHandler(request, response, new TokenIllegalException(null));
            }
        }
    }

    private void exceptionHandler(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) {
        failureHandler.onAuthenticationFailure(req, res, e);
    }

    /**
     * 刷新令牌
     *
     * @param currentLoginInfo    当前登陆信息
     * @param httpServletResponse 响应
     */
    private void refreshToken(CurrentLoginInfo currentLoginInfo, HttpServletResponse httpServletResponse) {
        String token = TokenUtil.generateToken(currentLoginInfo.getLoginCacheInfo().getUsername(), currentLoginInfo.getSessionToken(), DateUtil.addYear(new Date(), 1));
        TokenUtil.notice(httpServletResponse, token);
    }
}
