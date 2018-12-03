package com.agile.common.properties;

/**
 * Created by 佟盟 on 2018/1/11
 */
public class DruidConfigProperty {
    private String type = "MYSQL";
    private String dataBaseName;
    private String dataBaseIp;
    private String dataBasePort = "3306";
    private String dataBaseUsername = "root";
    private String dataBasePassword;
    private String dataBaseUrlParam = "serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL";
    private int initSize = 1;
    private int minIdle = 1;
    private int maxActive = 100;
    private int maxWait = 60000;
    private boolean removeAbandoned = true;
    private int removeAbandonedTimeout = 300000;
    private int timeBetweenEvictionRunsMillis = 60000;
    private int minEvictableIdleTimeMillis = 300000;
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = true;
    private int maxPoolPreparedStatementPerConnectionSize = 20;
    private String filters = "stat,wall";
    private boolean globalDataSourceStat = true;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getDataBaseIp() {
        return dataBaseIp;
    }

    public void setDataBaseIp(String dataBaseIp) {
        this.dataBaseIp = dataBaseIp;
    }

    public String getDataBasePort() {
        return dataBasePort;
    }

    public void setDataBasePort(String dataBasePort) {
        this.dataBasePort = dataBasePort;
    }

    public String getDataBaseUsername() {
        return dataBaseUsername;
    }

    public void setDataBaseUsername(String dataBaseUsername) {
        this.dataBaseUsername = dataBaseUsername;
    }

    public String getDataBasePassword() {
        return dataBasePassword;
    }

    public void setDataBasePassword(String dataBasePassword) {
        this.dataBasePassword = dataBasePassword;
    }

    public String getDataBaseUrlParam() {
        return dataBaseUrlParam;
    }

    public void setDataBaseUrlParam(String dataBaseUrlParam) {
        this.dataBaseUrlParam = dataBaseUrlParam;
    }

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public boolean isGlobalDataSourceStat() {
        return globalDataSourceStat;
    }

    public void setGlobalDataSourceStat(boolean globalDataSourceStat) {
        this.globalDataSourceStat = globalDataSourceStat;
    }
}
