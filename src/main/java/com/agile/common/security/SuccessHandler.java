package com.agile.common.security;

import cloud.agileframework.common.util.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/7/9
 */
public class SuccessHandler implements AuthenticationSuccessHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.info(String.format("成功登录[令牌：%s]", JSONUtil.toJSONString(authentication)));
    }
}
