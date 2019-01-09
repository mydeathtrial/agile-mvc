package com.agile.common.config;

import com.agile.common.properties.KafkaConsumerProperties;
import com.agile.common.util.PropertiesUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

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
@Conditional(KafkaConsumerConfig.class)
public class KafkaConsumerConfig implements Condition {
    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties());
    }

    private Map<String, Object> consumerProperties() {
        final int length = 7;
        Map<String, Object> props = new HashMap<>(length);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConsumerProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConsumerProperties.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, KafkaConsumerProperties.getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, KafkaConsumerProperties.getAutoCommitIntervalMs());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, KafkaConsumerProperties.getSessionTimeoutMs());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaConsumerProperties.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaConsumerProperties.getValueDeserializer());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConsumerProperties.getAutoOffsetReset());
        return props;
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final int concurrency = 3;
        final int pollTimeout = 3000;
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        return factory;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.kafka.consumer.enable", boolean.class);
    }
}
