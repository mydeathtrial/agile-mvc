package com.agile.common.generator;

import com.agile.common.util.*;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.*;

/**
 * Created by mydeathtrial on 2017/4/20
 */
public class AgileGenerator {

    static Map<String, Object> tableHandle(Map<String, Object> table) {
        //结果集
        Map<String, Object> data = new HashMap<>();

        //字段集
        List<HashMap<String, String>> columnList = new ArrayList<>();

        //导入集
        List<String> importList = new ArrayList<>();

        //表名
        String tableName = table.get("TABLE_NAME").toString();

        data.put("tableComment", table.get("REMARKS"));

        List<Map<String, Object>> columns = DataBaseUtil.listColumns(PropertiesUtil.getProperty("agile.druid.type"), PropertiesUtil.getProperty("agile.druid.data_base_ip"), PropertiesUtil.getProperty("agile.druid.data_base_post"), PropertiesUtil.getProperty("agile.druid.data_base_name"), PropertiesUtil.getProperty("agile.druid.data_base_username"), PropertiesUtil.getProperty("agile.druid.data_base_password"), tableName);

        for (Map<String, Object> column:columns){
            //参数容器
            HashMap<String, String> param = new HashMap<>();
            String isPrimaryKey = "false"; //是否为主键

            //字段名称
            String columnName = Objects.requireNonNull(MapUtil.getString(column, "COLUMN_NAME")).toLowerCase();

            //字段类型
            String columnType = MapUtil.getString(column,"TYPE_NAME");

            //属性名
            String propertyName = StringUtil.toLowerName(columnName);

            //属性名
            String propertyType = PropertiesUtil.getProperty("agile.generator.column_type." + columnType.toLowerCase());

            //处理主键
            try {
                if (Boolean.valueOf(column.get("IS_PRIMARY_KEY").toString())) {
                    //是否为主键
                    isPrimaryKey = "true";

                    //转包装类
                    propertyType = ClassUtil.toWrapperNameFromName(propertyType);
                }
            }catch (Exception ignored){
            }


            //是否自增长
            param.put("isAutoincrement", MapUtil.getString(column,"IS_AUTOINCREMENT"));

            //字段大小
            param.put("columnSize", MapUtil.getString(column,"COLUMN_SIZE"));

            //小数精度
            param.put("digits", MapUtil.getString(column,"DECIMAL_DIGITS"));

            //是否可为空
            param.put("nullable", "0".equals(MapUtil.getString(column,"NULLABLE")) ? "false" : "true");

            //字段默认值
            param.put("columnDef", MapUtil.getString(column,"COLUMN_DEF"));

            //表中的列的索引
            param.put("ordinalPosition", MapUtil.getString(column,"ORDINAL_POSITION"));

            //备注
            param.put("remarks", Objects.requireNonNull(MapUtil.getString(column, "REMARKS")).replaceAll("\r\n[ ]+"," "));

            param.put("columnName", columnName);
            param.put("columnType", columnType);
            param.put("propertyName", propertyName);
            param.put("propertyType", propertyType);
            param.put("propertyTypeOfSwagger", ClassUtil.toSwaggerTypeFromName(propertyType));
            param.put("isPrimaryKey", isPrimaryKey);
            param.put("getMethod", "get" + StringUtil.toUpperName(columnName));
            param.put("setMethod", "set" + StringUtil.toUpperName(columnName));

            //API导入
            switch (propertyType){
                case "Timestamp":importList.add("java.sql.Timestamp;");break;
                case "Date":importList.add("java.util.Date;");break;
            }

            columnList.add(param);
        }

        //文件导入
        data.put("importList", importList);

        //参数
        data.put("tableName", tableName);
        data.put("schemaName", null);
        data.put("catalogName", PropertiesUtil.getProperty("agile.druid.data_base_name"));

        //字段参数
        data.put("columnList", columnList);

        String entityClassName = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.entity_prefix")) + StringUtil.toUpperName(tableName) + StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.entity_suffix"));
        String serviceClassName = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.service_prefix")) + StringUtil.toUpperName(tableName) + StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.service_suffix"));
        data.put("entityClassName",entityClassName);
        data.put("serviceClassName",serviceClassName);

        return data;
    }

    /**
     * 生成文件
     * @param data 数据源
     */
    private static void generateAllFile(Map<String, Object> data) throws IOException, TemplateException {

        //Entity生成器
        generateFile(data,"agile.generator.entity_url","entityClassName","Entity.ftl","entityPackage");

        //service生成器
        generateFile(data,"agile.generator.service_url","serviceClassName","Service.ftl","servicePackage");
    }

    public static void generateFile(Map<String, Object> data,String filePathKey,String fileNameKey,String templateName,String packNameCacheKey) throws IOException, TemplateException {
        String url = PropertiesUtil.getProperty(filePathKey).trim().replaceAll("[\\\\]+","/");
        if(!url.endsWith("/"))url += "/";
        String fileName = data.get(fileNameKey) + ".java";
        data.put(packNameCacheKey, getPackPath(url));
        FreemarkerUtil.generatorProxy(templateName,url,fileName,data,false);
    }

    /**
     * 推测生成java文件的包名
     * @param url 生成目标文件存储路径
     * @return 包名
     */
    private static String getPackPath(String url){
        String javaPath = "src/main/java";
        if(!url.contains(javaPath))return null;
        int endIndex = 0;
        if(url.endsWith("/")){
            endIndex = 1;
        }
        String packPath = url.substring(url.indexOf(javaPath) + javaPath.length() + 1, url.length() - endIndex).replaceAll("/",".");
        if(packPath.isEmpty())return null;
        return packPath;
    }

    public static void main(String[] args) {
        try {
            String dbIndexKey = "agile.generator.db_index";
            String tableName = "agile.generator.table_name";
            String druidKey = "agile.druid";

            //获取表信息
            Integer dbIndex = null;
            if(PropertiesUtil.properties.containsKey(dbIndexKey)){
                dbIndex = PropertiesUtil.getProperty(dbIndexKey, int.class);
            }
            String dbType,ip,port,dbName,username,password;
            if(dbIndex != null){
                druidKey += String.format("[%s]",dbIndex);
            }

            dbType = String.format("%s.type",druidKey);
            ip = String.format("%s.data_base_ip",druidKey);
            port = String.format("%s.data_base_post",druidKey);
            dbName = String.format("%s.data_base_name",druidKey);
            username = String.format("%s.data_base_username",druidKey);
            password = String.format("%s.data_base_password",druidKey);

            List<Map<String, Object>> tables = DataBaseUtil.listTables(PropertiesUtil.getProperty(dbType), PropertiesUtil.getProperty(ip), PropertiesUtil.getProperty(port), PropertiesUtil.getProperty(dbName), PropertiesUtil.getProperty(username), PropertiesUtil.getProperty(password), PropertiesUtil.getProperty(tableName));
            for (Map<String, Object> table:tables){
                generateAllFile(tableHandle(table));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
