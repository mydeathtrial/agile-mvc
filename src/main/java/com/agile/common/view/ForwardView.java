package com.agile.common.view;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/3/26
 *
 * @author 佟盟
 */
public class ForwardView extends AbstractView {

    private static final String PREFIX = "Parameter_";
    private String url;

    public ForwardView(String url) {
        this.url = url;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Map.Entry<String, Object> entity : model.entrySet()) {
            request.setAttribute(PREFIX + entity.getKey(), entity.getValue());
        }
        //处理参数
        RequestDispatcher rd = this.getRequestDispatcher(request, getUrl());
        if (rd == null) {
            throw new ServletException("转发失败，地址:" + getUrl());
        }
        rd.forward(request, response);
    }

    @Nullable
    private RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
        return request.getRequestDispatcher(path);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static String getPrefix() {
        return PREFIX;
    }
}
