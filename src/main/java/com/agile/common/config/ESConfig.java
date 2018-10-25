package com.agile.common.config;

import com.agile.common.annotation.Init;
import com.agile.common.container.WebInitializer;
import com.agile.common.properties.ESProperties;
import com.agile.common.util.PropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.net.InetAddress;
/**
 * Created by 佟盟 on 2018/10/18
 */
@Configuration
public class ESConfig {
    private Log logger = com.agile.common.factory.LoggerFactory.createLogger("elasticsearch", WebInitializer.class, Level.DEBUG,Level.ERROR);

    @Bean
    public Client esClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", ESProperties.getClusterName()).put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        String nodeInfo = ESProperties.getClusterNodes();
        if(nodeInfo!=null){
            String[] nodes = nodeInfo.split(",");
            for (String node:nodes) {
                String[] info = node.split(":");
                if(info.length!=2)continue;
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(info[0]), Integer.parseInt(info[1])));
            }
        }

        return client;
    }

    @Init
    public void initEnv(){
        if(PropertiesUtil.getProperty("agile.elasticsearch.enable", boolean.class)) {
            try {
                initES();
            } catch (Exception e) {
                logger.error("ES环境初始化配置失败", e);
            }
        }
    }

    private void initES() throws NoSuchFieldException, IllegalAccessException {
        Class<com.idss.common.datafactory.utils.ESConfig> clazz = com.idss.common.datafactory.utils.ESConfig.class;
        Field clusterName = clazz.getDeclaredField("clusterName");
        clusterName.setAccessible(true);
        clusterName.set(null, ESProperties.getClusterName());

        Field clusterNodes = clazz.getDeclaredField("clusterNodes");
        clusterNodes.setAccessible(true);
        clusterNodes.set(null,ESProperties.getClusterNodes());

        Field clusterHosts = clazz.getDeclaredField("clusterHosts");
        clusterHosts.setAccessible(true);
        clusterHosts.set(null,ESProperties.getClusterHosts());

        Field poolSize = clazz.getDeclaredField("poolSize");
        poolSize.setAccessible(true);
        poolSize.set(null,ESProperties.getPoolSize());

        Field indexDateFormat = clazz.getDeclaredField("indexDateFormat");
        indexDateFormat.setAccessible(true);
        indexDateFormat.set(null,ESProperties.getIndexDateFormate());

        Field timeField = clazz.getDeclaredField("timeField");
        timeField.setAccessible(true);
        timeField.set(null,ESProperties.getTimeField());

        Field idField = clazz.getDeclaredField("idField");
        idField.setAccessible(true);
        idField.set(null,ESProperties.getIdField());

        Field rawMsgFiled = clazz.getDeclaredField("rawMsgFiled");
        rawMsgFiled.setAccessible(true);
        rawMsgFiled.set(null,ESProperties.getRawMsgField());

        Field scrollTimeValue = clazz.getDeclaredField("scrollTimeValue");
        scrollTimeValue.setAccessible(true);
        scrollTimeValue.set(null,ESProperties.getScrollTimeValue());

        Field scrollSetSize = clazz.getDeclaredField("scrollSetSize");
        scrollSetSize.setAccessible(true);
        scrollSetSize.set(null,ESProperties.getScrollSetSize());
    }
}
