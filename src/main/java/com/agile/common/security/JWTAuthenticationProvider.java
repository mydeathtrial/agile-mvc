package com.agile.common.security;

import com.agile.common.exception.RepeatAccount;
import com.agile.common.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by 佟盟 on 2017/1/13
 */
@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private PasswordEncoder passwordEncoder;
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public JWTAuthenticationProvider(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder(4);
    }

    /**
     * 登录验证
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getDetails() instanceof SecurityUser) return authentication;

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

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    /**
     * 校验密码
     */
    private void checkPassword(Authentication authentication, UserDetails user) {
        if (authentication.getCredentials() == null) {
            this.logger.debug("未找到密码");
            throw new BadCredentialsException(null);
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!this.passwordEncoder.matches(presentedPassword, user.getPassword())) {
                this.logger.debug("密码匹配失败");
                throw new BadCredentialsException(null);
            }
        }
    }

    /**
     * 登陆策略
     */
    private void loginStrategyHandler(UserDetails user) {
        Object salts = CacheUtil.get(user.getUsername() + "_SALT");
        if (salts != null && !salts.toString().isEmpty()) {
            switch (((SecurityUser) user).getOnLineStrategy()) {
                case "1000":
                    CacheUtil.evict(user.getUsername() + "_SALT");
                    break;
                case "2000":
                    break;
                default:
                    throw new RepeatAccount(null);
            }
        }
    }
}