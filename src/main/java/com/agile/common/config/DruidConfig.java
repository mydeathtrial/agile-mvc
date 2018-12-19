package com.agile.common.config;

import com.agile.common.exception.NonSupportDBException;
import com.agile.common.filter.DruidFilter;
import com.agile.common.properties.DBConfigProperties;
import com.agile.common.properties.DruidConfigProperty;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.util.Collections;

/**
 * Created by 佟盟 on 2017/10/7
 */
@Configuration
public class DruidConfig {
    private static int index = 0;

    private DruidConfigProperty druidConfigProperty;

    public DruidConfig() {
        this.druidConfigProperty = DBConfigProperties.getDruid().get(index);
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    DruidDataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();

        StringBuilder druidUrl = new StringBuilder();
        String db = druidConfigProperty.getType().toLowerCase();
        switch (db) {
            case "mysql":
                druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                druidDataSource.setValidationQuery("SELECT 1");
                druidUrl.append("jdbc:mysql://").append(druidConfigProperty.getDataBaseIp()).append(":").append(druidConfigProperty.getDataBasePort()).append("/").append(druidConfigProperty.getDataBaseName()).append("?").append(druidConfigProperty.getDataBaseUrlParam());
                break;
            case "oracle":
                druidDataSource.setDriverClassName("oracle.jdbc.OracleDriver");
                druidDataSource.setValidationQuery("SELECT 'x' FROM DUAL");
                druidUrl.append("jdbc:oracle:thin:@").append(druidConfigProperty.getDataBaseIp()).append(":").append(druidConfigProperty.getDataBasePort()).append(":").append(druidConfigProperty.getDataBaseName());
                break;
            case "mariadb":
                druidDataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                druidDataSource.setValidationQuery("SELECT 1");
                druidUrl.append("jdbc:mariadb://").append(druidConfigProperty.getDataBaseIp()).append(":").append(druidConfigProperty.getDataBasePort()).append("/").append(druidConfigProperty.getDataBaseName()).append("?").append(druidConfigProperty.getDataBaseUrlParam());
                break;
            default:
                try {
                    throw new NonSupportDBException();
                } catch (NonSupportDBException e) {
                    e.printStackTrace();
                }
        }

        druidDataSource.setUrl(druidUrl.toString());
        druidDataSource.setUsername(druidConfigProperty.getDataBaseUsername());
        druidDataSource.setPassword(druidConfigProperty.getDataBasePassword());
        druidDataSource.setInitialSize(druidConfigProperty.getInitSize());
        druidDataSource.setMinIdle(druidConfigProperty.getMinIdle());
        druidDataSource.setMaxActive(druidConfigProperty.getMaxActive());
        druidDataSource.setMaxWait(druidConfigProperty.getMaxWait());
        druidDataSource.setRemoveAbandoned(druidConfigProperty.isRemoveAbandoned());
        druidDataSource.setRemoveAbandonedTimeout(druidConfigProperty.getRemoveAbandonedTimeout());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfigProperty.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidConfigProperty.getMinEvictableIdleTimeMillis());

        druidDataSource.setTestWhileIdle(druidConfigProperty.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidConfigProperty.isTestOnBorrow());
        druidDataSource.setTestOnReturn(druidConfigProperty.isTestOnReturn());
        druidDataSource.setFilters(druidConfigProperty.getFilters());
        druidDataSource.setPoolPreparedStatements(druidConfigProperty.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfigProperty.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setUseGlobalDataSourceStat(druidConfigProperty.isGlobalDataSourceStat());

        druidDataSource.setProxyFilters(Collections.singletonList(new DruidFilter()));
        return druidDataSource;

    }
}
