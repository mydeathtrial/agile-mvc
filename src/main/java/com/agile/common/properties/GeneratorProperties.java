package com.agile.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/1/31 9:39
 * @Description TODO
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.generator")
@Setter
@Getter
public class GeneratorProperties {
    /**
     * 实体文件生成到的目录地址
     */
    private String entityUrl;
    /**
     * Service文件生成到的目录地址
     */
    private String serviceUrl;
    /**
     * 测试文件生成到的目录地址
     */
    private String testUrl;
    /**
     * 实体类生成名前缀
     */
    private String entityPrefix;
    /**
     * 实体类生成名后缀
     */
    private String entitySuffix = "Entity";
    /**
     * service类生成名前缀
     */
    private String servicePrefix;
    /**
     * service类生成名后缀
     */
    private String serviceSuffix = "Service";
    /**
     * 测试文件类生成名前缀
     */
    private String testPrefix;
    /**
     * 测试文件类生成名后缀
     */
    private String testSuffix = "Test";
    /**
     * 代码生成器目标表名字,可逗号分隔,可like百分号形式模糊匹配
     */
    private String tableName;
    /**
     * 表名大小写是否敏感
     */
    private boolean isSensitive = false;
    /**
     * 数据库字段类型与java映射规则
     */
    private Map<String, String> columnType = new HashMap<>();

    public String getJavaType(String type) {
        return columnType.get(type);
    }

    public GeneratorProperties() {
        columnType.put("bigint", "java.lang.Long");
        columnType.put("bit", "java.lang.Boolean");
        columnType.put("char", "java.lang.String");
        columnType.put("datetime", "java.util.Date");
        columnType.put("time", "java.util.Date");
        columnType.put("date", "java.util.Date");
        columnType.put("mediumtext", "java.lang.String");
        columnType.put("bolb", "byte[]");
        columnType.put("clob", "java.lang.String");
        columnType.put("decimal", "java.lang.Double");
        columnType.put("double", "java.lang.Double");
        columnType.put("float", "java.lang.Float");
        columnType.put("image", "byte[]");
        columnType.put("int", "java.lang.Integer");
        columnType.put("longblob", "java.lang.Byte");
        columnType.put("money", "java.lang.Double");
        columnType.put("nchar", "java.lang.String");
        columnType.put("number", "java.math.BigDecimal");
        columnType.put("numeric", "java.lang.Double");
        columnType.put("nvarchar", "java.lang.String");
        columnType.put("real", "java.lang.Double");
        columnType.put("smallint", "java.lang.Double");
        columnType.put("text", "java.lang.String");
        columnType.put("timestamp", "java.util.Date");
        columnType.put("tinyint", "java.lang.Integer");
        columnType.put("varchar", "java.lang.String");
        columnType.put("varchar2", "java.lang.String");
        columnType.put("tinytext", "java.lang.String");
        columnType.put("longtext", "java.lang.String");
        columnType.put("character", "java.lang.String");
    }
}
