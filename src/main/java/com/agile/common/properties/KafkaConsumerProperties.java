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
@Properties(prefix = "agile.kafka.consumer")
public class KafkaConsumerProperties {

    private static String groupId;
    private static String maxPollRecords;
    private static String maxPollIntervalMs;
    private static String sessionTimeoutMs;
    private static String heartbeatIntervalMs;
    private static String bootstrapServers;
    private static String enableAutoCommit;
    private static String autoCommitIntervalMs;
    private static String partitionAssignmentStrategy;
    private static String autoOffsetReset;
    private static String fetchMinBytes;
    private static String fetchMaxBytes;
    private static String fetchMaxWaitMs;
    private static String metadataMaxAgeMs;
    private static String maxPartitionFetchBytes;
    private static String sendBufferBytes;
    private static String receiveBufferBytes;
    private static String clientId;
    private static String reconnectBackoffMs;
    private static String reconnectBackoffMaxMs;
    private static String retryBackoffMs;
    private static String metricsSampleWindowMs;
    private static String metricsNumSamples;
    private static String metricsRecordingLevel;
    private static String metricReporters;
    private static String checkCrcs;
    private static String keyDeserializer;
    private static String valueDeserializer;
    private static String connectionsMaxIdleMs;
    private static String requestTimeoutMs;
    private static String defaultApiTimeoutMs;
    private static String interceptorClasses;
    private static String excludeInternalTopics;
    private static String internalLeaveGroupOnClose;
    private static String isolationLevel;

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupId) {
        KafkaConsumerProperties.groupId = groupId;
    }

    public static String getMaxPollRecords() {
        return maxPollRecords;
    }

    public static void setMaxPollRecords(String maxPollRecords) {
        KafkaConsumerProperties.maxPollRecords = maxPollRecords;
    }

    public static String getMaxPollIntervalMs() {
        return maxPollIntervalMs;
    }

    public static void setMaxPollIntervalMs(String maxPollIntervalMs) {
        KafkaConsumerProperties.maxPollIntervalMs = maxPollIntervalMs;
    }

    public static String getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public static void setSessionTimeoutMs(String sessionTimeoutMs) {
        KafkaConsumerProperties.sessionTimeoutMs = sessionTimeoutMs;
    }

    public static String getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public static void setHeartbeatIntervalMs(String heartbeatIntervalMs) {
        KafkaConsumerProperties.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public static String getBootstrapServers() {
        return bootstrapServers;
    }

    public static void setBootstrapServers(String bootstrapServers) {
        KafkaConsumerProperties.bootstrapServers = bootstrapServers;
    }

    public static String getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public static void setEnableAutoCommit(String enableAutoCommit) {
        KafkaConsumerProperties.enableAutoCommit = enableAutoCommit;
    }

    public static String getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public static void setAutoCommitIntervalMs(String autoCommitIntervalMs) {
        KafkaConsumerProperties.autoCommitIntervalMs = autoCommitIntervalMs;
    }

    public static String getPartitionAssignmentStrategy() {
        return partitionAssignmentStrategy;
    }

    public static void setPartitionAssignmentStrategy(String partitionAssignmentStrategy) {
        KafkaConsumerProperties.partitionAssignmentStrategy = partitionAssignmentStrategy;
    }

    public static String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public static void setAutoOffsetReset(String autoOffsetReset) {
        KafkaConsumerProperties.autoOffsetReset = autoOffsetReset;
    }

    public static String getFetchMinBytes() {
        return fetchMinBytes;
    }

    public static void setFetchMinBytes(String fetchMinBytes) {
        KafkaConsumerProperties.fetchMinBytes = fetchMinBytes;
    }

    public static String getFetchMaxBytes() {
        return fetchMaxBytes;
    }

    public static void setFetchMaxBytes(String fetchMaxBytes) {
        KafkaConsumerProperties.fetchMaxBytes = fetchMaxBytes;
    }

    public static String getFetchMaxWaitMs() {
        return fetchMaxWaitMs;
    }

    public static void setFetchMaxWaitMs(String fetchMaxWaitMs) {
        KafkaConsumerProperties.fetchMaxWaitMs = fetchMaxWaitMs;
    }

    public static String getMetadataMaxAgeMs() {
        return metadataMaxAgeMs;
    }

    public static void setMetadataMaxAgeMs(String metadataMaxAgeMs) {
        KafkaConsumerProperties.metadataMaxAgeMs = metadataMaxAgeMs;
    }

    public static String getMaxPartitionFetchBytes() {
        return maxPartitionFetchBytes;
    }

    public static void setMaxPartitionFetchBytes(String maxPartitionFetchBytes) {
        KafkaConsumerProperties.maxPartitionFetchBytes = maxPartitionFetchBytes;
    }

    public static String getSendBufferBytes() {
        return sendBufferBytes;
    }

    public static void setSendBufferBytes(String sendBufferBytes) {
        KafkaConsumerProperties.sendBufferBytes = sendBufferBytes;
    }

    public static String getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public static void setReceiveBufferBytes(String receiveBufferBytes) {
        KafkaConsumerProperties.receiveBufferBytes = receiveBufferBytes;
    }

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        KafkaConsumerProperties.clientId = clientId;
    }

    public static String getReconnectBackoffMs() {
        return reconnectBackoffMs;
    }

    public static void setReconnectBackoffMs(String reconnectBackoffMs) {
        KafkaConsumerProperties.reconnectBackoffMs = reconnectBackoffMs;
    }

    public static String getReconnectBackoffMaxMs() {
        return reconnectBackoffMaxMs;
    }

    public static void setReconnectBackoffMaxMs(String reconnectBackoffMaxMs) {
        KafkaConsumerProperties.reconnectBackoffMaxMs = reconnectBackoffMaxMs;
    }

    public static String getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public static void setRetryBackoffMs(String retryBackoffMs) {
        KafkaConsumerProperties.retryBackoffMs = retryBackoffMs;
    }

    public static String getMetricsSampleWindowMs() {
        return metricsSampleWindowMs;
    }

    public static void setMetricsSampleWindowMs(String metricsSampleWindowMs) {
        KafkaConsumerProperties.metricsSampleWindowMs = metricsSampleWindowMs;
    }

    public static String getMetricsNumSamples() {
        return metricsNumSamples;
    }

    public static void setMetricsNumSamples(String metricsNumSamples) {
        KafkaConsumerProperties.metricsNumSamples = metricsNumSamples;
    }

    public static String getMetricsRecordingLevel() {
        return metricsRecordingLevel;
    }

    public static void setMetricsRecordingLevel(String metricsRecordingLevel) {
        KafkaConsumerProperties.metricsRecordingLevel = metricsRecordingLevel;
    }

    public static String getMetricReporters() {
        return metricReporters;
    }

    public static void setMetricReporters(String metricReporters) {
        KafkaConsumerProperties.metricReporters = metricReporters;
    }

    public static String getCheckCrcs() {
        return checkCrcs;
    }

    public static void setCheckCrcs(String checkCrcs) {
        KafkaConsumerProperties.checkCrcs = checkCrcs;
    }

    public static String getKeyDeserializer() {
        return keyDeserializer;
    }

    public static void setKeyDeserializer(String keyDeserializer) {
        KafkaConsumerProperties.keyDeserializer = keyDeserializer;
    }

    public static String getValueDeserializer() {
        return valueDeserializer;
    }

    public static void setValueDeserializer(String valueDeserializer) {
        KafkaConsumerProperties.valueDeserializer = valueDeserializer;
    }

    public static String getConnectionsMaxIdleMs() {
        return connectionsMaxIdleMs;
    }

    public static void setConnectionsMaxIdleMs(String connectionsMaxIdleMs) {
        KafkaConsumerProperties.connectionsMaxIdleMs = connectionsMaxIdleMs;
    }

    public static String getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public static void setRequestTimeoutMs(String requestTimeoutMs) {
        KafkaConsumerProperties.requestTimeoutMs = requestTimeoutMs;
    }

    public static String getDefaultApiTimeoutMs() {
        return defaultApiTimeoutMs;
    }

    public static void setDefaultApiTimeoutMs(String defaultApiTimeoutMs) {
        KafkaConsumerProperties.defaultApiTimeoutMs = defaultApiTimeoutMs;
    }

    public static String getInterceptorClasses() {
        return interceptorClasses;
    }

    public static void setInterceptorClasses(String interceptorClasses) {
        KafkaConsumerProperties.interceptorClasses = interceptorClasses;
    }

    public static String getExcludeInternalTopics() {
        return excludeInternalTopics;
    }

    public static void setExcludeInternalTopics(String excludeInternalTopics) {
        KafkaConsumerProperties.excludeInternalTopics = excludeInternalTopics;
    }

    public static String getInternalLeaveGroupOnClose() {
        return internalLeaveGroupOnClose;
    }

    public static void setInternalLeaveGroupOnClose(String internalLeaveGroupOnClose) {
        KafkaConsumerProperties.internalLeaveGroupOnClose = internalLeaveGroupOnClose;
    }

    public static String getIsolationLevel() {
        return isolationLevel;
    }

    public static void setIsolationLevel(String isolationLevel) {
        KafkaConsumerProperties.isolationLevel = isolationLevel;
    }
}
