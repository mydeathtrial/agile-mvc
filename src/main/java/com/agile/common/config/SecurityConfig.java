package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.security.FailureHandler;
import com.agile.common.security.JwtAuthenticationProvider;
import com.agile.common.security.LoginFilter;
import com.agile.common.security.LogoutHandler;
import com.agile.common.security.SecurityUserDetailsService;
import com.agile.common.security.TokenFilter;
import com.agile.common.security.TokenStrategy;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
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

import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private String[] immuneUrl;

    private final Dao dao;

    @Autowired
    public SecurityConfig(Dao dao) {
        this.dao = dao;
        immuneUrl = new String[]{
                "/static/**",
                "/favicon.ico",
                PropertiesUtil.getProperty("agile.druid.url") + "/**",
                PropertiesUtil.getProperty("agile.security.login_url"),
                PropertiesUtil.getProperty("agile.kaptcha.url"),
                "/actuator/**",
                "/actuator",
                "/jolokia"};
        this.immuneUrl = (String[]) ArrayUtil.addAll(immuneUrl, PropertiesUtil.getProperty("agile.security.exclude_url").split(Constant.RegularAbout.COMMA));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers(immuneUrl).permitAll().anyRequest().authenticated()
                .and().logout().logoutUrl(SecurityProperties.getLoginOutUrl()).deleteCookies(SecurityProperties.getTokenHeader()).logoutSuccessHandler(logoutHandler())
                .and().exceptionHandling().accessDeniedHandler(new FailureHandler())
                .and().cors().configurationSource(corsConfigurationSource())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().none()
                .and().csrf().disable().httpBasic().disable()
                .addFilterAt(tokenFilter(), LogoutFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return source;
    }

    @Bean
    CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_headers").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowedOrigins(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_origin").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowedMethods(ArrayUtil.asList(PropertiesUtil.getProperty("agile.servlet.allow_methods").split(Constant.RegularAbout.COMMA)));
        corsConfiguration.setAllowCredentials(PropertiesUtil.getProperty("agile.servlet.allow_credentials", boolean.class));
        return corsConfiguration;
    }

    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        final int cacheSeconds = 10;
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:com/agile/conf/language");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(cacheSeconds);
        return messageSource;
    }

    @Bean
    SecurityUserDetailsService securityUserDetailsService() {
        return new SecurityUserDetailsService(dao);
    }

    @Bean
    TokenStrategy tokenStrategy() {
        return new TokenStrategy(securityUserDetailsService());
    }

    @Bean
    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(securityUserDetailsService());
    }

    @Bean
    LoginFilter loginFilter() {
        return new LoginFilter(jwtAuthenticationProvider(), tokenStrategy());
    }

    @Bean
    TokenFilter tokenFilter() {
        return new TokenFilter(securityUserDetailsService(), immuneUrl);
    }

    @Bean
    LogoutHandler logoutHandler() {
        return new LogoutHandler();
    }
}
