package com.agile.common.properties;

import com.agile.common.base.Constant;
import com.agile.common.util.DataBaseUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.agile.common.util.DataBaseUtil.DB.MYSQL;

/**
 * @author 佟盟
 */
@ConfigurationProperties(prefix = "agile.druid")
@Setter
@Getter
public class DruidConfigProperties {
    private DataBaseUtil.DB type = MYSQL;
    private String dataBaseUrl = "${spring.datasource.url}";
    private String validationQuery = "SELECT 1";
    private String dataBaseName;
    private String dataBaseIp = "127.0.0.1";
    private String dataBasePort = "3306";
    private String dataBaseUsername = "root";
    private String dataBasePassword;
    private String dataBaseUrlParam = "serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL";
    private int initSize = Constant.NumberAbout.ONE;
    private int minIdle = Constant.NumberAbout.ONE;
    private int maxActive = Constant.NumberAbout.HUNDRED;
    private int maxWait = (int) (Constant.NumberAbout.SIX * Math.pow(Constant.NumberAbout.HUNDRED, Constant.NumberAbout.TWO));
    private boolean removeAbandoned = true;
    private int removeAbandonedTimeout = (int) (Constant.NumberAbout.THREE * Math.pow(Constant.NumberAbout.HUNDRED, Constant.NumberAbout.TWO));
    private int timeBetweenEvictionRunsMillis = (int) (Constant.NumberAbout.SIX * Math.pow(Constant.NumberAbout.HUNDRED, Constant.NumberAbout.TWO));
    private int minEvictableIdleTimeMillis = (int) (Constant.NumberAbout.THREE * Math.pow(Constant.NumberAbout.TEN, Constant.NumberAbout.FIVE));
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = true;
    private int maxPoolPreparedStatementPerConnectionSize = Constant.NumberAbout.TWO * Constant.NumberAbout.TEN;
    private String filters = "stat,wall";
    private boolean globalDataSourceStat = true;
    private String exclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";
    private String managerName = "admin";
    private String managerPassword = "admin";
    private boolean resetEnable = true;
    private long sessionStatMaxCount = (long) Math.pow(Constant.NumberAbout.TEN, Constant.NumberAbout.FIVE);
    private String url = "/druid";
}
