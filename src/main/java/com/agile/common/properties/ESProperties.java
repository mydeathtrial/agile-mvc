package com.agile.common.properties;

import com.agile.common.annotation.Properties;

/**
 * Created by 佟盟 on 2018/10/24
 */
@Properties(prefix = "agile.elasticsearch")
public class ESProperties {
    private static String clusterName;
    private static String clusterNodes;
    private static String clusterHosts;
    private static int poolSize;
    private static String indexDateFormat;
    private static String timeField;
    private static String idField;
    private static String rawMsgFiled;
    private static int scrollTimeValue;
    private static int scrollSetSize;

    public static String getClusterName() {
        return clusterName;
    }

    public static void setClusterName(String clusterName) {
        ESProperties.clusterName = clusterName;
    }

    public static String getClusterNodes() {
        return clusterNodes;
    }

    public static void setClusterNodes(String clusterNodes) {
        ESProperties.clusterNodes = clusterNodes;
    }

    public static String getClusterHosts() {
        return clusterHosts;
    }

    public static void setClusterHosts(String clusterHosts) {
        ESProperties.clusterHosts = clusterHosts;
    }

    public static String getIndexDateFormat() {
        return indexDateFormat;
    }

    public static void setIndexDateFormat(String indexDateFormat) {
        ESProperties.indexDateFormat = indexDateFormat;
    }

    public static String getTimeField() {
        return timeField;
    }

    public static void setTimeField(String timeField) {
        ESProperties.timeField = timeField;
    }

    public static String getIdField() {
        return idField;
    }

    public static void setIdField(String idField) {
        ESProperties.idField = idField;
    }

    public static String getRawMsgFiled() {
        return rawMsgFiled;
    }

    public static void setRawMsgFiled(String rawMsgFiled) {
        ESProperties.rawMsgFiled = rawMsgFiled;
    }

    public static int getPoolSize() {
        return poolSize;
    }

    public static void setPoolSize(int poolSize) {
        ESProperties.poolSize = poolSize;
    }

    public static int getScrollTimeValue() {
        return scrollTimeValue;
    }

    public static void setScrollTimeValue(int scrollTimeValue) {
        ESProperties.scrollTimeValue = scrollTimeValue;
    }

    public static int getScrollSetSize() {
        return scrollSetSize;
    }

    public static void setScrollSetSize(int scrollSetSize) {
        ESProperties.scrollSetSize = scrollSetSize;
    }
}
