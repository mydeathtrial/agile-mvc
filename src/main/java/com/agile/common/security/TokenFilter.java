package com.agile.common.security;

import com.agile.common.cache.Cache;
import com.agile.common.exception.NoSignInException;
import com.agile.common.exception.TokenIllegalException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆验证码拦截器
 *
 * @author 佟盟 on 2017/9/27
 */
public class TokenFilter extends OncePerRequestFilter {
    private final FailureHandler failureHandler;
    private final CustomerUserDetailsService securityUserDetailsService;
    private RequestMatcher[] matches;

    private SecurityProperties securityProperties;

    private Cache tokenCache;

    public TokenFilter(CustomerUserDetailsService securityUserDetailsService, String[] immuneUrl, SecurityProperties securityProperties) {
        matches = new RequestMatcher[immuneUrl.length];
        int i = 0;
        while (i < immuneUrl.length) {
            matches[i] = new AntPathRequestMatcher(immuneUrl[i]);
            i++;
        }
        this.failureHandler = new FailureHandler();
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityProperties = securityProperties;
        tokenCache = CacheUtil.getCache(securityProperties.getTokenHeader());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (!requiresAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = TokenUtil.getToken(request, securityProperties.getTokenHeader());
            if (StringUtil.isBlank(token)) {
                throw new NoSignInException("账号尚未登陆，服务中无法获取登陆信息");
            }

            LoginTokenInfo loginTokenInfo = TokenUtil.initLoginTokenInfo(token);

            if (!TokenUtil.validateToken(token)) {
                throw new TokenIllegalException("身份令牌已过期");
            }

            if (ObjectUtil.isEmpty(loginTokenInfo.getAuthentication())) {
                throw new TokenIllegalException("身份令牌验证失败");
            }

            //判断策略
            if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {

                if (!loginTokenInfo.getSessionPasswordCacheValue().contains(loginTokenInfo.getSessionPassword())) {
                    throw new TokenIllegalException("身份令牌验证失败");
                }

                CustomerUserDetails userDetails = (CustomerUserDetails) loginTokenInfo.getAuthentication().getDetails();
                securityUserDetailsService.validate(userDetails);

                String currentSaltValue = userDetails.getPassword();
                if (!loginTokenInfo.getSaltValue().equals(currentSaltValue)) {
                    throw new TokenIllegalException("身份令牌验证失败，密码已修改");
                }
                refreshToken(loginTokenInfo, response);
            }

            SecurityContextHolder.getContext().setAuthentication(loginTokenInfo.getAuthentication());
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

    private void refreshToken(LoginTokenInfo loginTokenInfo, ServletResponse httpServletResponse) {
        CustomerUserDetails userDetails = (CustomerUserDetails) loginTokenInfo.getAuthentication().getDetails();

        String newSessionPassword = Long.toString(IdUtil.generatorId());
        securityUserDetailsService.updateLoginInfo(userDetails.getUsername(), loginTokenInfo.getSessionPassword(), newSessionPassword);
        tokenCache.put(loginTokenInfo.getSessionPasswordCacheKey(), loginTokenInfo.refreshSessionPassword(newSessionPassword));

        String agileToken = TokenUtil.generateToken(loginTokenInfo.getUserInfoCacheKey(), newSessionPassword, userDetails.getPassword());
        HttpServletResponse response = (HttpServletResponse) httpServletResponse;
        response.setHeader(securityProperties.getTokenHeader(), agileToken);
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        for (RequestMatcher matcher : matches) {
            if (matcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
