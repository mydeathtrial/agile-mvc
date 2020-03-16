package com.agile.common.security;

import com.agile.common.exception.TokenIllegalException;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.TokenUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登录验证码拦截器
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

            //根据令牌---提取当前登录信息
            CurrentLoginInfo currentLoginInfo = LoginCacheInfo.getCurrentLoginInfo(token);

            //验证当前登陆用户信息合法性
            LoginCacheInfo.validateCacheDate(currentLoginInfo.getLoginCacheInfo());

            //账户信息赋给业务层
            SecurityContextHolder.getContext().setAuthentication(currentLoginInfo.getLoginCacheInfo().getAuthentication());

            //执行业务层程序
            filterChain.doFilter(request, response);

            //判断策略，复杂令牌时刷新token
            if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
                String newToken = LoginCacheInfo.refreshToken(currentLoginInfo);
                TokenUtil.notice(request, response, newToken, null);
            }
        } catch (Exception e) {
            if (!(e instanceof AuthenticationException)) {
                e = new TokenIllegalException(e.getMessage());
            }
            failureHandler.onAuthenticationFailure(request, response, (AuthenticationException) e);
        }
    }
}
