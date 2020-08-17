package com.agile.common.security;

import cloud.agileframework.spring.util.spring.BeanUtil;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.ParamUtil;
import com.agile.common.util.ViewUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/7/6
 */
public class LogoutHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private CustomerUserDetailsService securityUserDetailsService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        //返回
        try {
            processingExit(request, response);
            ViewUtil.render(new Head(RETURN.LOGOUT_SUCCESS), null, request, response);
        } catch (Exception e) {
            LoggerFactory.AUTHORITY_LOG.debug(e);
        }

    }

    /**
     * 处理退出
     *
     * @param request  请求
     * @param response 响应
     */
    public void processingExit(HttpServletRequest request, HttpServletResponse response) {
        //获取令牌
        String token = ParamUtil.getInfo(request, securityProperties.getTokenHeader());
        if (token == null) {
            return;
        }

        //执行前钩子
        before(token);

        //获取当前登录信息
        CurrentLoginInfo currentLoginInfo = LoginCacheInfo.getCurrentLoginInfo(token);
        String username = currentLoginInfo.getLoginCacheInfo().getUsername();
        String sessionToken = Long.toString(currentLoginInfo.getSessionToken());

        //更新数据库
        securityUserDetailsService.stopLoginInfo(username, sessionToken);

        //更新缓存
        LoginCacheInfo.remove(currentLoginInfo);

        //清空header信息
        clearHeader(response);

        //执行后钩子
        after(token);

        LoggerFactory.AUTHORITY_LOG.info(String.format("账号退出[username:%s][token：%s]", username, sessionToken));
    }


    /**
     * 清空头部信息
     *
     * @param httpServletResponse 响应
     */
    private void clearHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), Constant.RegularAbout.BLANK);
    }

    /**
     * 清空cookies
     */
    private void cleanCookie() {
//        Cookie cookie = new Cookie(securityProperties.getTokenHeader(), null);
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(0);
//        cookie.setPath(Constant.RegularAbout.SLASH);
//        httpServletResponse.addCookie(cookie);
    }

    private void before(String token) {
        ObjectProvider<LoginOutProcessor> observers = BeanUtil.getApplicationContext().getBeanProvider(LoginOutProcessor.class);
        observers.stream().forEach(node -> {
                    try {
                        node.before(token);
                    } catch (Exception ignored) {
                    }
                }
        );
    }

    private void after(String token) {
        ObjectProvider<LoginOutProcessor> observers = BeanUtil.getApplicationContext().getBeanProvider(LoginOutProcessor.class);
        observers.stream().forEach(node -> {
            try {
                node.after(token);
            } catch (Exception ignored) {
            }
        });
    }
}
