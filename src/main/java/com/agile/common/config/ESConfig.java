package com.agile.common.config;

import com.agile.common.annotation.Init;
import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.EsProperties;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.net.InetAddress;

/**
 * @author 佟盟 on 2018/10/18
 */
@Configuration
@ConditionalOnClass({com.idss.common.datafactory.utils.ESConfig.class})
@EnableConfigurationProperties(value = {EsProperties.class})
@Conditional(ESConfig.class)
public class ESConfig implements Condition {
    @Autowired
    private EsProperties esProperties;

    @Bean
    public Client esClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", esProperties.getClusterName()).put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        String nodeInfo = esProperties.getClusterNodes();
        final int length = 2;
        if (nodeInfo != null) {
            String[] nodes = nodeInfo.split(Constant.RegularAbout.COMMA);
            for (String node : nodes) {
                String[] info = node.split(Constant.RegularAbout.COLON);
                if (info.length != length) {
                    continue;
                }
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(info[0]), Integer.parseInt(info[1])));
            }
        }

        return client;
    }

    @Init
    public void initEnv() {
        final String enable = "agile.elasticsearch.enable";
        if (PropertiesUtil.getProperty(enable, boolean.class)) {
            try {
                ObjectUtil.copyProperties(esProperties, new com.idss.common.datafactory.utils.ESConfig());
            } catch (Exception e) {
                LoggerFactory.ES_LOG.error("ES环境初始化配置失败", e);
            }
        }
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.elasticsearch.enable", boolean.class, "false");
    }
}
