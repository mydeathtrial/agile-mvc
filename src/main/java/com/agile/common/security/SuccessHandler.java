package com.agile.common.security;

import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.ViewUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author 佟盟 on 2018/7/9
 */
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        assert RETURN.SUCCESS != null;
        try {
            SecurityProperties securityProperties = FactoryUtil.getBean(SecurityProperties.class);
            assert securityProperties != null;
            String token = response.getHeader(securityProperties.getTokenHeader());

            ViewUtil.render(new Head(RETURN.SUCCESS), new HashMap<String, Object>(1) {{
                put("token", token);
                put("realName", ((CustomerUserDetails) authentication.getDetails()).getName());
                put("detail", authentication);
            }}, request, response);
            LoggerFactory.AUTHORITY_LOG.info(String.format("成功登陆[令牌：%s]", token));
        } catch (Exception e) {
            LoggerFactory.AUTHORITY_LOG.debug(StringUtil.coverToString(e));
        }
    }
}
