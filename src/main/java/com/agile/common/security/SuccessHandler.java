package com.agile.common.security;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.JSONUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/7/9
 */
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoggerFactory.AUTHORITY_LOG.info(String.format("成功登录[令牌：%s]", JSONUtil.toJSONString(authentication)));
    }
}
