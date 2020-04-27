package com.agile.common.filter;

import com.agile.common.base.Constant;
import com.agile.common.util.string.StringUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author 佟盟 on 2017/9/25
 */
public class CorsFilter extends OncePerRequestFilter implements Filter {
    private String allowOrigin;
    private String allowMethods;
    private String allowCredentials;
    private String allowHeaders;
    private String exposeHeaders;

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (allowOrigin.equals(Constant.RegularAbout.SNOW) || StringUtil.isEmpty(allowOrigin)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
        } else {
            List<String> allowOriginList = Arrays.asList(allowOrigin.split(Constant.RegularAbout.COMMA));
            String currentOrigin = httpServletRequest.getHeader("Origin");
            if (allowOriginList.contains(currentOrigin)) {
                httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, currentOrigin.replaceAll("\\s", Constant.RegularAbout.BLANK));
            }
        }
        if (StringUtil.isNotEmpty(allowOrigin)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin.replaceAll("\\s", Constant.RegularAbout.BLANK));
        }
        if (StringUtil.isNotEmpty(allowMethods)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_METHODS, allowMethods.replaceAll("\\s", Constant.RegularAbout.BLANK));
        }
        if (StringUtil.isNotEmpty(allowCredentials)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials.replaceAll("\\s", Constant.RegularAbout.BLANK));
        }
        if (StringUtil.isNotEmpty(allowHeaders)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders.replaceAll("\\s", Constant.RegularAbout.BLANK));
        }
        if (StringUtil.isNotEmpty(exposeHeaders)) {
            httpServletResponse.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, exposeHeaders.replaceAll("\\s", Constant.RegularAbout.BLANK));
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }
}