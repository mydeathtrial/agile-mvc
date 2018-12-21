package com.agile.common.config;

import com.agile.common.properties.KaptchaConfigProperties;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.Random;

/**
 * @author 佟盟 on 2017/10/7
 */
@Configuration
public class KaptchaConfig extends Configurable implements TextProducer {

    @Bean
    DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties()));
        return defaultKaptcha;
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", KaptchaConfigProperties.getBorder());
        properties.setProperty("kaptcha.border.color", KaptchaConfigProperties.getBorderColor());
        properties.setProperty("kaptcha.textproducer.font.color", KaptchaConfigProperties.getTextproducerFontColor());
        properties.setProperty("kaptcha.textproducer.font.size", KaptchaConfigProperties.getTextproducerFontSize());
        properties.setProperty("kaptcha.image.width", KaptchaConfigProperties.getImageWidth());
        properties.setProperty("kaptcha.image.height", KaptchaConfigProperties.getImageHeight());
        properties.setProperty("kaptcha.textproducer.char.length", KaptchaConfigProperties.getTextproducerCharLength());
        properties.setProperty("kaptcha.textproducer.font.names", KaptchaConfigProperties.getTextproducerFontNames());
        properties.setProperty("kaptcha.textproducer.impl", "com.agile.common.config.KaptchaConfig");
        return properties;
    }

    @Override
    public String getText() {
        int length = getConfig().getTextProducerCharLength();
        String text = KaptchaConfigProperties.getText();
        if (text == null) {
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
}
