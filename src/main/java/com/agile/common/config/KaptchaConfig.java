package com.agile.common.config;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.kaptcha.KaptchaServlet;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.Configurable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.http.HttpServlet;
import java.util.Properties;
import java.util.Random;

/**
 * @author 佟盟 on 2017/10/7
 */
@Configuration
@EnableConfigurationProperties(value = {KaptchaConfigProperties.class})
@Conditional(KaptchaConfig.class)
public class KaptchaConfig extends Configurable implements TextProducer, Condition {

    @Autowired
    private KaptchaConfigProperties kaptchaConfigProperties;


    @Bean
    public ServletRegistrationBean kaptchaServlet() {
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化登录验证码");
        }

        ServletRegistrationBean<HttpServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new KaptchaServlet());
        reg.addUrlMappings(kaptchaConfigProperties.getUrl());
        return reg;
    }

    @Bean
    DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties()));
        return defaultKaptcha;
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", kaptchaConfigProperties.getBorder());
        properties.setProperty("kaptcha.border.color", kaptchaConfigProperties.getBorderColor());
        properties.setProperty("kaptcha.textproducer.font.color", kaptchaConfigProperties.getTextproducerFontColor());
        properties.setProperty("kaptcha.textproducer.font.size", kaptchaConfigProperties.getTextproducerFontSize());
        properties.setProperty("kaptcha.image.width", kaptchaConfigProperties.getImageWidth());
        properties.setProperty("kaptcha.image.height", kaptchaConfigProperties.getImageHeight());
        properties.setProperty("kaptcha.textproducer.char.length", kaptchaConfigProperties.getTextproducerCharLength());
        properties.setProperty("kaptcha.textproducer.font.names", kaptchaConfigProperties.getTextproducerFontNames());
        properties.setProperty("kaptcha.textproducer.impl", "com.agile.common.config.KaptchaConfig");
        return properties;
    }

    @Override
    public String getText() {
        int length = getConfig().getTextProducerCharLength();
        KaptchaConfigProperties properties = FactoryUtil.getBean(KaptchaConfigProperties.class);
        assert properties != null;
        String text = properties.getText();
        if (StringUtil.isEmpty(text)) {
            return defaultGetText();
        }
        char[] s = text.toCharArray();
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int ind = rand.nextInt(s.length);
            sb.append(s[ind]);
        }
        return sb.toString();
    }

    private String defaultGetText() {
        int length = this.getConfig().getTextProducerCharLength();
        char[] chars = this.getConfig().getTextProducerCharString();
        Random rand = new Random();
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < length; ++i) {
            text.append(chars[rand.nextInt(chars.length)]);
        }

        return text.toString();
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.kaptcha.enable", boolean.class, "false");
    }
}
