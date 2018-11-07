package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.security.*;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * Created by 佟盟 on 2017/9/26
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private LoginFilter loginFilter;
    private TokenFilter tokenFilter;
    private LogoutHandler logoutHandler;
    public static String[] immuneUrl = new String[]{
            "/static/**",
            "/favicon.ico",
            PropertiesUtil.getProperty("agile.druid.url")+"/**",
            SecurityProperties.getLoginUrl(),
            KaptchaConfigProperties.getUrl(),
            "/actuator/**",
            "/actuator",
            "/jolokia"
    };

    static {
        immuneUrl = (String[]) ArrayUtil.addAll(immuneUrl,SecurityProperties.getExcludeUrl().split(Constant.RegularAbout.COMMA));
    }

    @Autowired
    public SecurityConfig(LoginFilter loginFilter, TokenFilter tokenFilter, LogoutHandler logoutHandler) {
        this.loginFilter = loginFilter;
        this.tokenFilter = tokenFilter;
        this.logoutHandler = logoutHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().antMatchers(immuneUrl).permitAll().anyRequest().authenticated()
            .and().logout().logoutUrl(SecurityProperties.getLoginOutUrl()).deleteCookies(SecurityProperties.getTokenHeader()).logoutSuccessHandler(logoutHandler)
            .and().exceptionHandling().accessDeniedHandler(new FailureHandler())
            .and().cors().configurationSource(corsConfigurationSource())
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().none()
            .and().csrf().disable().httpBasic().disable()
            .addFilterAt(tokenFilter, LogoutFilter.class)
            .addFilterAt(loginFilter,UsernamePasswordAuthenticationFilter.class)
        ;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration());
        return source;
    }

    @Bean
    CorsConfiguration corsConfiguration(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_headers").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowedOrigins(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_origin").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowedMethods(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_methods").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowCredentials(PropertiesUtil.getProperty("agile.servlet.allow_credentials",boolean.class));
        return corsConfiguration;
    }

    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:com/agile/conf/language");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(10);
        return messageSource;
    }

}
