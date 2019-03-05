package com.agile.common.config;

import com.agile.common.base.Constant;
import com.agile.common.util.ResourceUtil;
import com.agile.common.util.StringUtil;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/3/5 9:37
 * @Description TODO
 * @since 1.0
 */
@Configuration
public class MessageResourceAutoConfiguration {
    @Bean
    @ConfigurationProperties(
            prefix = "spring.messages"
    )
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) throws IOException {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        Resource[] basenameFiles = ResourceUtil.getResources("**/" + properties.getBasename() + "*", "properties");
        List<String> list = new ArrayList<>();
        String[] s = properties.getBasename().split("[/]");
        String messageName = s[s.length - Constant.NumberAbout.ONE];
        assert basenameFiles != null;
        for (Resource resource : basenameFiles) {
            String node = ResourceUtil.getClassPath(resource);
            if (StringUtil.isBlank(node)) {
                continue;
            }
            int messageNameIndex = node.indexOf(messageName) + messageName.length();
            String ignore = node.substring(messageNameIndex);
            node = node.substring(Constant.NumberAbout.ZERO, node.indexOf(ignore));
            if (StringUtil.isBlank(node)) {
                continue;
            }
            list.add(node);
        }
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(list.toArray(new String[]{}));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }
}
