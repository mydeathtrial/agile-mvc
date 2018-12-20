package com.agile.common.config;

import com.agile.common.annotation.Init;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.ESProperties;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by 佟盟 on 2018/10/18
 */
@Configuration
public class ESConfig {

    @Bean
    public Client esClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", ESProperties.getClusterName()).put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        String nodeInfo = ESProperties.getClusterNodes();
        final int length = 2;
        if (nodeInfo != null) {
            String[] nodes = nodeInfo.split(",");
            for (String node : nodes) {
                String[] info = node.split(":");
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
        if (PropertiesUtil.getProperty("agile.elasticsearch.enable", boolean.class)) {
            try {
                ObjectUtil.copyProperties(new ESProperties(), new com.idss.common.datafactory.utils.ESConfig());
            } catch (Exception e) {
                LoggerFactory.getEsLog().error("ES环境初始化配置失败", e);
            }
        }
    }

}
