package com.agile.common.config;

import com.agile.common.properties.KafkaProducerProperties;
import com.agile.common.util.PropertiesUtil;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * <p>创建时间：2019/1/4<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableKafka
@Conditional(KafkaProducerConfig.class)
public class KafkaProducerConfig implements Condition {

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    private Map<String, Object> producerProperties() {
        final int length = 8;
        Map<String, Object> props = new HashMap<>(length);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProducerProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaProducerProperties.getKeyDeserializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProducerProperties.getValueDeserializer());
        props.put(ProducerConfig.RETRIES_CONFIG, KafkaProducerProperties.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, KafkaProducerProperties.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, KafkaProducerProperties.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, KafkaProducerProperties.getBufferMemory());
        props.put(ProducerConfig.ACKS_CONFIG, KafkaProducerProperties.getAcks());
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory(), true);
        kafkaTemplate.setDefaultTopic(KafkaProducerProperties.getDefaultTopic());
        return kafkaTemplate;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.kafka.producer.enable", boolean.class);
    }
}
