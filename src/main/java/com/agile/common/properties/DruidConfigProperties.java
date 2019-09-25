package com.agile.common.properties;

import com.agile.common.base.Constant;
import com.agile.common.filter.DruidFilter;
import com.agile.common.util.DataBaseUtil;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

import static com.agile.common.util.DataBaseUtil.DB.MYSQL;

/**
 * @author 佟盟
 */
@ConfigurationProperties(prefix = "agile.druid")
public class DruidConfigProperties extends DruidDataSource {

    /**
     * 数据库类型,用于agile自动组装数据库连接使用
     */
    @Setter
    @Getter
    private DataBaseUtil.DB type = MYSQL;

    /**
     * 数据库名字
     */
    @Setter
    @Getter
    private String dataBaseName;

    /**
     * 数据库IP地址
     */
    @Setter
    @Getter
    private String dataBaseIp = "127.0.0.1";

    /**
     * 数据库端口
     */
    @Setter
    @Getter
    private String dataBasePort = "3306";

    /**
     * 数据库连接时提供的连接参数
     */
    @Setter
    @Getter
    private String dataBaseUrlParam = "serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL&tinyInt1isBit=false";

    /**
     * druid监控排除静态资源请求
     */
    @Setter
    @Getter
    private String exclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";

    /**
     * druid监控登录账号
     */
    @Setter
    @Getter
    private String managerName = "admin";

    /**
     * druid监控登录密码
     */
    @Setter
    @Getter
    private String managerPassword = "admin";

    /**
     * druid监控能否重置
     */
    @Setter
    @Getter
    private boolean resetEnable = true;

    /**
     * 最大session个数
     */
    @Setter
    @Getter
    private long sessionStatMaxCount = (long) Math.pow(Constant.NumberAbout.TEN, Constant.NumberAbout.FIVE);

    /**
     * druid监控地址
     */
    @Setter
    @Getter
    private String dashboardUrl = "/druid";

    public void setType(DataBaseUtil.DB type) {
        this.type = type;
        this.setDriverClassName(type.getDriver());
        this.setValidationQuery(type.getTestSql());
    }

    @Override
    public void setProxyFilters(List<Filter> filters) {
        super.setProxyFilters(Collections.singletonList(new DruidFilter()));
    }

}
