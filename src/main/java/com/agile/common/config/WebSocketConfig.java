package com.agile.common.config;

import com.agile.common.util.PropertiesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 描述：
 * <p>创建时间：2018/12/3<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Configuration
@Conditional(WebSocketConfig.class)
public class WebSocketConfig implements Condition {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.websocket.enable", boolean.class);
    }
}
