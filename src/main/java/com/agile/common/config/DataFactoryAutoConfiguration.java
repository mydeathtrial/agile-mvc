package com.agile.common.config;

import com.agile.common.annotation.Init;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.EsProperties;
import com.agile.common.util.ObjectUtil;
import com.idss.common.datafactory.utils.ESConfig;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟 on 2018/10/18
 */
@Configuration
@ConditionalOnClass({ESConfig.class, ElasticsearchClient.class})
@EnableConfigurationProperties(value = {EsProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.elasticsearch", havingValue = "false")
public class DataFactoryAutoConfiguration {
    private final EsProperties esProperties;

    @Autowired
    public DataFactoryAutoConfiguration(EsProperties esProperties) {
        this.esProperties = esProperties;
    }

    @Init
    public void initEnv() {
        try {
            ObjectUtil.copyProperties(esProperties, new com.idss.common.datafactory.utils.ESConfig());
        } catch (Exception e) {
            assert LoggerFactory.ES_LOG != null;
            LoggerFactory.ES_LOG.error("ES环境初始化配置失败", e);
        }
    }
}
