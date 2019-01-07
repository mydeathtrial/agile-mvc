package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * 描述：
 * <p>创建时间：2019/1/4<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Properties(prefix = "agile.kafka.producer")
public class KafkaProducerProperties {

    private static String bootstrapServers;
    private static String metadataMaxAgeMs;
    private static String batchSize;
    private static String acks;
    private static String lingerMs;
    private static String clientId;
    private static String sendBufferBytes;
    private static String receiveBufferBytes;
    private static String maxRequestSize;
    private static String reconnectBackoffMs;
    private static String reconnectBackoffMaxMs;
    private static String maxBlockMs;
    private static String bufferMemory;
    private static String retryBackoffMs;
    private static String compressionType;
    private static String metricsSampleWindowMs;
    private static String metricsNumSamples;
    private static String metricsRecordingLevel;
    private static String metricReporters;
    private static String maxInFlightRequestsPerConnection;
    private static String retries;
    private static String keyDeserializer;
    private static String valueDeserializer;
    private static String connectionsMaxIdleMs;
    private static String partitionerClass;
    private static String requestTimeoutMs;
    private static String interceptorClasses;
    private static String enableIdempotence;
    private static String transactionTimeoutMs;
    private static String transactionalId;
    private static String defaultTopic;

    public static String getBootstrapServers() {
        return bootstrapServers;
    }

    public static void setBootstrapServers(String bootstrapServers) {
        KafkaProducerProperties.bootstrapServers = bootstrapServers;
    }

    public static String getMetadataMaxAgeMs() {
        return metadataMaxAgeMs;
    }

    public static void setMetadataMaxAgeMs(String metadataMaxAgeMs) {
        KafkaProducerProperties.metadataMaxAgeMs = metadataMaxAgeMs;
    }

    public static String getBatchSize() {
        return batchSize;
    }

    public static void setBatchSize(String batchSize) {
        KafkaProducerProperties.batchSize = batchSize;
    }

    public static String getAcks() {
        return acks;
    }

    public static void setAcks(String acks) {
        KafkaProducerProperties.acks = acks;
    }

    public static String getLingerMs() {
        return lingerMs;
    }

    public static void setLingerMs(String lingerMs) {
        KafkaProducerProperties.lingerMs = lingerMs;
    }

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        KafkaProducerProperties.clientId = clientId;
    }

    public static String getSendBufferBytes() {
        return sendBufferBytes;
    }

    public static void setSendBufferBytes(String sendBufferBytes) {
        KafkaProducerProperties.sendBufferBytes = sendBufferBytes;
    }

    public static String getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public static void setReceiveBufferBytes(String receiveBufferBytes) {
        KafkaProducerProperties.receiveBufferBytes = receiveBufferBytes;
    }

    public static String getMaxRequestSize() {
        return maxRequestSize;
    }

    public static void setMaxRequestSize(String maxRequestSize) {
        KafkaProducerProperties.maxRequestSize = maxRequestSize;
    }

    public static String getReconnectBackoffMs() {
        return reconnectBackoffMs;
    }

    public static void setReconnectBackoffMs(String reconnectBackoffMs) {
        KafkaProducerProperties.reconnectBackoffMs = reconnectBackoffMs;
    }

    public static String getReconnectBackoffMaxMs() {
        return reconnectBackoffMaxMs;
    }

    public static void setReconnectBackoffMaxMs(String reconnectBackoffMaxMs) {
        KafkaProducerProperties.reconnectBackoffMaxMs = reconnectBackoffMaxMs;
    }

    public static String getMaxBlockMs() {
        return maxBlockMs;
    }

    public static void setMaxBlockMs(String maxBlockMs) {
        KafkaProducerProperties.maxBlockMs = maxBlockMs;
    }

    public static String getBufferMemory() {
        return bufferMemory;
    }

    public static void setBufferMemory(String bufferMemory) {
        KafkaProducerProperties.bufferMemory = bufferMemory;
    }

    public static String getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public static void setRetryBackoffMs(String retryBackoffMs) {
        KafkaProducerProperties.retryBackoffMs = retryBackoffMs;
    }

    public static String getCompressionType() {
        return compressionType;
    }

    public static void setCompressionType(String compressionType) {
        KafkaProducerProperties.compressionType = compressionType;
    }

    public static String getMetricsSampleWindowMs() {
        return metricsSampleWindowMs;
    }

    public static void setMetricsSampleWindowMs(String metricsSampleWindowMs) {
        KafkaProducerProperties.metricsSampleWindowMs = metricsSampleWindowMs;
    }

    public static String getMetricsNumSamples() {
        return metricsNumSamples;
    }

    public static void setMetricsNumSamples(String metricsNumSamples) {
        KafkaProducerProperties.metricsNumSamples = metricsNumSamples;
    }

    public static String getMetricsRecordingLevel() {
        return metricsRecordingLevel;
    }

    public static void setMetricsRecordingLevel(String metricsRecordingLevel) {
        KafkaProducerProperties.metricsRecordingLevel = metricsRecordingLevel;
    }

    public static String getMetricReporters() {
        return metricReporters;
    }

    public static void setMetricReporters(String metricReporters) {
        KafkaProducerProperties.metricReporters = metricReporters;
    }

    public static String getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    public static void setMaxInFlightRequestsPerConnection(String maxInFlightRequestsPerConnection) {
        KafkaProducerProperties.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    public static String getRetries() {
        return retries;
    }

    public static void setRetries(String retries) {
        KafkaProducerProperties.retries = retries;
    }

    public static String getKeyDeserializer() {
        return keyDeserializer;
    }

    public static void setKeyDeserializer(String keyDeserializer) {
        KafkaProducerProperties.keyDeserializer = keyDeserializer;
    }

    public static String getValueDeserializer() {
        return valueDeserializer;
    }

    public static void setValueDeserializer(String valueDeserializer) {
        KafkaProducerProperties.valueDeserializer = valueDeserializer;
    }

    public static String getConnectionsMaxIdleMs() {
        return connectionsMaxIdleMs;
    }

    public static void setConnectionsMaxIdleMs(String connectionsMaxIdleMs) {
        KafkaProducerProperties.connectionsMaxIdleMs = connectionsMaxIdleMs;
    }

    public static String getPartitionerClass() {
        return partitionerClass;
    }

    public static void setPartitionerClass(String partitionerClass) {
        KafkaProducerProperties.partitionerClass = partitionerClass;
    }

    public static String getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public static void setRequestTimeoutMs(String requestTimeoutMs) {
        KafkaProducerProperties.requestTimeoutMs = requestTimeoutMs;
    }

    public static String getInterceptorClasses() {
        return interceptorClasses;
    }

    public static void setInterceptorClasses(String interceptorClasses) {
        KafkaProducerProperties.interceptorClasses = interceptorClasses;
    }

    public static String getEnableIdempotence() {
        return enableIdempotence;
    }

    public static void setEnableIdempotence(String enableIdempotence) {
        KafkaProducerProperties.enableIdempotence = enableIdempotence;
    }

    public static String getTransactionTimeoutMs() {
        return transactionTimeoutMs;
    }

    public static void setTransactionTimeoutMs(String transactionTimeoutMs) {
        KafkaProducerProperties.transactionTimeoutMs = transactionTimeoutMs;
    }

    public static String getTransactionalId() {
        return transactionalId;
    }

    public static void setTransactionalId(String transactionalId) {
        KafkaProducerProperties.transactionalId = transactionalId;
    }

    public static String getDefaultTopic() {
        return defaultTopic;
    }

    public static void setDefaultTopic(String defaultTopic) {
        KafkaProducerProperties.defaultTopic = defaultTopic;
    }
}
