package com.agile.common.security;

import com.agile.common.cache.AgileCache;
import com.agile.common.exception.RepeatAccount;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.PasswordUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟 on 2017/1/13
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private CustomerUserDetailsService userDetailsService;

    public JwtAuthenticationProvider(CustomerUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 登录验证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getDetails() instanceof UserDetails) {
            return authentication;
        }

        String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();

        //加载用户数据
        UserDetails user = userDetailsService.loadUserByUsername(username);

        //验证账号合法性
        userDetailsService.validate(user);

        //验证密码
        checkPassword(authentication, user);

        //判断登陆策略
        loginStrategyHandler(user);

        //设置详情，用于token策略
        ((UsernamePasswordAuthenticationToken) authentication).setDetails(user);

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    /**
     * 校验密码
     */
    private void checkPassword(Authentication authentication, UserDetails user) {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("未找到密码");
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            String cipher = user.getPassword();
            if (!PasswordUtil.decryption(presentedPassword, cipher)) {
                throw new BadCredentialsException(String.format("密码匹配失败,[输入项：%s][目标值：%s]", presentedPassword, cipher));
            }
        }
    }

    /**
     * 登陆策略
     */
    private void loginStrategyHandler(UserDetails user) {
        SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);
        assert securityProperties != null;
        AgileCache cache = CacheUtil.getCache(securityProperties.getTokenHeader());

        LoginCacheInfo loginCacheInfo = cache.get(user.getUsername(), LoginCacheInfo.class);
        if (loginCacheInfo != null && loginCacheInfo.getSessionTokens().size() > 0) {
            switch (((CustomerUserDetails) user).getLoginStrategy()) {
                case SINGLETON_REPLACE:
                    cache.evict(user.getUsername());
                    break;
                case MORE:
                    break;
                default:
                    throw new RepeatAccount("重复账户登陆");
            }
        }
    }
}