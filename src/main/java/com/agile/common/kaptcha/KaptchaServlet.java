package com.agile.common.kaptcha;

import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.util.ServletUtil;
import com.google.code.kaptcha.Producer;

import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 佟盟 on 2018/9/6
 */
public class KaptchaServlet extends HttpServlet implements Servlet {
    private Producer kaptchaProducer;

    private KaptchaConfigProperties kaptchaConfigProperties = FactoryUtil.getBean(KaptchaConfigProperties.class);

    @Override
    public void init(ServletConfig conf) {
        this.kaptchaProducer = FactoryUtil.getBean(Producer.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        initResponse(resp);
        String capText = createCode(req, resp);
        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        ServletOutputStream out = resp.getOutputStream();
        ImageIO.write(bi, "jpg", out);
    }

    private void initResponse(HttpServletResponse resp) {
        resp.setDateHeader("Expires", 0L);
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setContentType("image/jpeg");
    }

    private String createCode(HttpServletRequest req, HttpServletResponse resp) {
        final int length = 20;
        String capText = this.kaptchaProducer.createText();

        String codeToken = ServletUtil.getInfo(req, kaptchaConfigProperties.getTokenHeader());
        if (codeToken == null) {
            codeToken = RandomStringUtil.getRandom(length, RandomStringUtil.Random.LETTER_UPPER);
        }
        CacheUtil.put(codeToken, capText, kaptchaConfigProperties.getLiveTime());
        setOutParam(kaptchaConfigProperties.getTokenHeader(), codeToken, resp);
        return capText;
    }

    private void setOutParam(String codeToken, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(codeToken, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setHeader(codeToken, value);
    }
}
