package com.agile.common.config;

import com.agile.common.properties.EsProperties;
import com.idss.common.datafactory.DataSearch;
import com.idss.common.datafactory.DataSearchFactory;
import com.idss.common.datafactory.utils.ESConfig;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟 on 2018/10/18
 */
@Configuration
@ConditionalOnClass({ESConfig.class, ElasticsearchClient.class})
@EnableConfigurationProperties(value = {EsProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.elasticsearch", havingValue = "true")
public class DataFactoryAutoConfiguration {
    private final EsProperties esProperties;

    @Autowired
    public DataFactoryAutoConfiguration(EsProperties esProperties) {
        this.esProperties = esProperties;
    }

    @Bean
    public DataSearch loadDataSearchBean() {
        ESConfig.init(this.esProperties.getConfig(), this.esProperties.getDefaultEsKey());
        return DataSearchFactory.buildDataSearch();
    }
}
