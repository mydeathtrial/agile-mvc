package com.agile.common.util;


import com.agile.common.factory.LoggerFactory;
import oracle.jdbc.OracleDriver;
import org.apache.commons.logging.Log;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author 佟盟 on 2018/9/7
 */
public class DataBaseUtil {
    private static Log logger = LoggerFactory.createLogger("sql", DataBaseUtil.class);
    private static Connection conn;

    /**
     * 根据字符串,判断数据库类型
     */
    private static DB parseDB(String dbType) {
        DB result;
        if (null == dbType || dbType.trim().length() < 1) {
            result = DB.EMPTY;
        }
        dbType = dbType.trim().toUpperCase();
        if (dbType.contains("ORACLE")) {
            try {
                DriverManager.registerDriver(OracleDriver.class.newInstance());
            } catch (InstantiationException | SQLException | IllegalAccessException e) {
                e.printStackTrace();
            }
            result = DB.ORACLE;
        } else if (dbType.contains("MYSQL")) {
            try {
                DriverManager.registerDriver(com.mysql.cj.jdbc.Driver.class.newInstance());
            } catch (IllegalAccessException | InstantiationException | SQLException e) {
                e.printStackTrace();
            }
            result = DB.MYSQL;
        } else if (dbType.contains("SQL") && dbType.contains("SERVER")) {
            if (dbType.contains("2005") || dbType.contains("2008") || dbType.contains("2012")) {
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                result = DB.SQL_SERVER2005;
            } else {
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                result = DB.SQL_SERVER;
            }
        } else if (dbType.contains("DB2")) {
            result = DB.DB2;
        } else if (dbType.contains("INFORMIX")) {
            result = DB.INFORMIX;
        } else if (dbType.contains("SYBASE")) {
            result = DB.SYBASE;
        } else {
            result = DB.OTHER;
        }

        return result;
    }

    private static ResultSet getResultSet(PATTERN type, String dbType, String ip, String port, String dbName, String username, String password, String pattern) {
        ip = trim(ip);
        port = trim(port);
        dbName = trim(dbName);
        username = trim(username);
        password = trim(password);
        DB dbtype = parseDB(dbType);
        String url = createDBUrl(dbtype, ip, port, dbName);
        try {
            conn = getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pattern = pattern == null ? "%" : pattern;
        ResultSet rs = null;
        try {
            // 获取Meta信息对象
            DatabaseMetaData meta = conn.getMetaData();
            // 数据库的用户
            String schemaPattern = null;

            switch (type) {
                case TABLE:
                    String[] types = {"TABLE", "VIEW"};

                    if (DB.ORACLE.equals(dbtype)) {
                        schemaPattern = username;
                        if (null != schemaPattern) {
                            schemaPattern = schemaPattern.toUpperCase();
                        }
                        rs = meta.getTables(null, schemaPattern, pattern, types);
                    } else if (DB.MYSQL.equals(dbtype)) {
                        schemaPattern = dbName;
                        rs = meta.getTables(schemaPattern, schemaPattern, pattern, types);
                    } else if (DB.SQL_SERVER.equals(dbtype) || DB.SQL_SERVER2005.equals(dbtype)) {
                        rs = meta.getTables(null, null, pattern, types);
                    } else if (DB.DB2.equals(dbtype)) {
                        schemaPattern = "jence_user";
                        rs = meta.getTables(null, schemaPattern, pattern, types);
                    } else if (DB.INFORMIX.equals(dbtype)) {
                        rs = meta.getTables(null, null, pattern, types);
                    } else if (DB.SYBASE.equals(dbtype)) {
                        rs = meta.getTables(null, null, pattern, types);
                    }
                    break;
                case COLUMN:
                    pattern = pattern.toUpperCase();
                    String columnNamePattern = null;
                    if (DB.ORACLE.equals(dbtype)) {
                        schemaPattern = username;
                        if (null != schemaPattern) {
                            schemaPattern = schemaPattern.toUpperCase();
                        }
                    }

                    rs = meta.getColumns(null, schemaPattern, pattern, columnNamePattern);
                    break;
                case PRIMARY_KEY:
                    pattern = pattern.toUpperCase();
                    if (DB.ORACLE.equals(dbtype)) {
                        schemaPattern = username;
                        if (null != schemaPattern) {
                            schemaPattern = schemaPattern.toUpperCase();
                        }
                    }

                    rs = meta.getPrimaryKeys(null, schemaPattern, pattern);
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 列出数据库的所有表
     */
    public static List<Map<String, Object>> listTables(String dbType, String ip, String port, String dbName, String username, String password, String tableName) {
        if (tableName.contains(",")) {
            String[] tables = tableName.replaceAll("((?![%-])\\W)+", ",").split(",");
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < tables.length; i++) {
                list.addAll(getDBInfo(PATTERN.TABLE, dbType, ip, port, dbName, username, password, tables[i]));
            }
            return list;
        }
        return getDBInfo(PATTERN.TABLE, dbType, ip, port, dbName, username, password, tableName.trim());
    }

    /**
     * 列出表的所有字段
     */
    public static List<Map<String, Object>> listColumns(String dbType, String ip, String port, String dbName, String username, String password, String tableName) {
        List<Map<String, Object>> list = getDBInfo(PATTERN.COLUMN, dbType, ip, port, dbName, username, password, tableName);
        List<Map<String, Object>> keyList = listPrimayKeys(dbType, ip, port, dbName, username, password, tableName);
        for (Map<String, Object> keyColumn : keyList) {
            for (Map<String, Object> column : list) {
                boolean isPrimaryKey = false;
                if (keyColumn.get("COLUMN_NAME").toString().equals(column.get("COLUMN_NAME").toString())) {
                    isPrimaryKey = true;
                }
                column.put("IS_PRIMARY_KEY", isPrimaryKey);
            }
        }
        return list;
    }

    /**
     * 列出表的所有主键
     */
    public static List<Map<String, Object>> listPrimayKeys(String dbType, String ip, String port, String dbName, String username, String password, String tableName) {
        return getDBInfo(PATTERN.PRIMARY_KEY, dbType, ip, port, dbName, username, password, tableName);
    }

    /**
     * 列出表的所有主键
     */
    public static List<Map<String, Object>> getDBInfo(PATTERN pattern, String dbType, String ip, String port, String dbName, String username, String password, String tableName) {
        List<Map<String, Object>> list = null;
        ResultSet rs = null;
        try {
            rs = getResultSet(pattern, dbType, ip, port, dbName, username, password, tableName);
            list = parseResultSetToMapList(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(conn);
        }
        return list;
    }

    /**
     * 根据IP,端口,以及数据库名字,拼接Oracle连接字符串
     */
    private static String createDBUrl(DB dbtype, String ip, String port, String dbname) {
        String url = "";
        if (DB.ORACLE.equals(dbtype)) {
            url += "jdbc:oracle:thin:@";
            url += ip.trim();
            url += ":" + port.trim();
            url += ":" + dbname;

            // hotbackup
//            String url2 = "";
//            url2 = url2+"jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = "
//                    + ip.trim() +")(PORT ="+ port.trim() +")))(CONNECT_DATA = (SERVICE_NAME ="+dbname+
//                    ")(FAILOVER_MODE = (TYPE = SELECT)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))";
//            url = url2;
        } else if (DB.MYSQL.equals(dbtype)) {
            url += "jdbc:mysql://";
            url += ip.trim();
            url += ":" + port.trim();
            url += "/" + dbname;
            url += "?" + "serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&allowPublicKeyRetrieval=true";
        } else if (DB.SQL_SERVER.equals(dbtype)) {
            url += "jdbc:jtds:sqlserver://";
            url += ip.trim();
            url += ":" + port.trim();
            url += "/" + dbname;
            url += ";tds=8.0;lastupdatecount=true";
        } else if (DB.SQL_SERVER2005.equals(dbtype)) {
            url += "jdbc:sqlserver://";
            url += ip.trim();
            url += ":" + port.trim();
            url += "; DatabaseName=" + dbname;
        } else if (DB.DB2.equals(dbtype)) {
            url += "jdbc:db2://";
            url += ip.trim();
            url += ":" + port.trim();
            url += "/" + dbname;
        } else if (DB.INFORMIX.equals(dbtype)) {
            url += "jdbc:informix-sqli://";
            url += ip.trim();
            url += ":" + port.trim();
            url += "/" + dbname;
        } else if (DB.SYBASE.equals(dbtype)) {
            url += "jdbc:sybase:Tds:";
            url += ip.trim();
            url += ":" + port.trim();
            url += "/" + dbname;
        } else {
            throw new RuntimeException("不认识的数据库类型!");
        }
        return url;
    }

    /**
     * 获取JDBC连接
     */
    private static Connection getConnection(String url, String username, String password) throws SQLException {
        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);
        // !!! Oracle 如果想要获取元数据 REMARKS 信息,需要加此参数
        info.put("remarksReporting", "true");
        // !!! MySQL 标志位, 获取TABLE元数据 REMARKS 信息
        info.put("useInformationSchema", "true");
        return DriverManager.getConnection(url, info);
    }

    /**
     * 将一个未处理的ResultSet解析为Map列表.
     */
    private static List<Map<String, Object>> parseResultSetToMapList(ResultSet rs) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (null == rs) {
            return null;
        }
        try {
            while (rs.next()) {
                Map<String, Object> map = parseResultSetToMap(rs);
                if (null != map) {
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析ResultSet的单条记录,不进行 ResultSet 的next移动处理
     */
    private static Map<String, Object> parseResultSetToMap(ResultSet rs) {
        if (null == rs) {
            return null;
        }
        final int length = 16;
        Map<String, Object> map = new HashMap<>(length);
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colNum = meta.getColumnCount();
            for (int i = 1; i <= colNum; i++) {
                // 列名
                String name = meta.getColumnLabel(i);
                Object value = rs.getObject(i);
                // 加入属性
                map.put(name, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static boolean tryLink(String databasetype, String ip, String port, String dbname, String username, String password) {
        DB dbtype = parseDB(databasetype);
        String url = createDBUrl(dbtype, ip, port, dbname);
        Connection connection = null;
        try {
            connection = getConnection(url, username, password);
            if (null == connection) {
                return false;
            }
            DatabaseMetaData meta = connection.getMetaData();
            return null != meta;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("数据库连接失败");
            }
            e.printStackTrace();
            System.exit(0);
        } finally {
            close(connection);
        }
        return false;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String trim(String str) {
        if (null != str) {
            str = str.trim();
        }
        return str;
    }

    // ResultSetMetaData 使用示例
    public static void demoResultSetMetaData(ResultSetMetaData data) throws SQLException {
        for (int i = 1; i <= data.getColumnCount(); i++) {
            // 获得所有列的数目及实际列数
            int columnCount = data.getColumnCount();
            // 获得指定列的列名
            String columnName = data.getColumnName(i);
            // 获得指定列的数据类型
            int columnType = data.getColumnType(i);
            // 获得指定列的数据类型名
            String columnTypeName = data.getColumnTypeName(i);
            // 所在的Catalog名字
            String catalogName = data.getCatalogName(i);
            // 对应数据类型的类
            String columnClassName = data.getColumnClassName(i);
            // 在数据库中类型的最大字符个数
            int columnDisplaySize = data.getColumnDisplaySize(i);
            // 默认的列的标题
            String columnLabel = data.getColumnLabel(i);
            // 获得列的模式
            String schemaName = data.getSchemaName(i);
            // 某列类型的精确度(类型的长度)
            int precision = data.getPrecision(i);
            // 小数点后的位数
            int scale = data.getScale(i);
            // 获取某列对应的表名
            String tableName = data.getTableName(i);
            // 是否自动递增
            boolean isAutoInctement = data.isAutoIncrement(i);
            // 在数据库中是否为货币型
            boolean isCurrency = data.isCurrency(i);
            // 是否为空
            int isNullable = data.isNullable(i);
            // 是否为只读
            boolean isReadOnly = data.isReadOnly(i);
            // 能否出现在where中
            boolean isSearchable = data.isSearchable(i);
            logger.info(columnCount);
            logger.info("获得列" + i + "的字段名称:" + columnName);
            logger.info("获得列" + i + "的类型,返回SqlType中的编号:" + columnType);
            logger.info("获得列" + i + "的数据类型名:" + columnTypeName);
            logger.info("获得列" + i + "所在的Catalog名字:" + catalogName);
            logger.info("获得列" + i + "对应数据类型的类:" + columnClassName);
            logger.info("获得列" + i + "在数据库中类型的最大字符个数:" + columnDisplaySize);
            logger.info("获得列" + i + "的默认的列的标题:" + columnLabel);
            logger.info("获得列" + i + "的模式:" + schemaName);
            logger.info("获得列" + i + "类型的精确度(类型的长度):" + precision);
            logger.info("获得列" + i + "小数点后的位数:" + scale);
            logger.info("获得列" + i + "对应的表名:" + tableName);
            logger.info("获得列" + i + "是否自动递增:" + isAutoInctement);
            logger.info("获得列" + i + "在数据库中是否为货币型:" + isCurrency);
            logger.info("获得列" + i + "是否为空:" + isNullable);
            logger.info("获得列" + i + "是否为只读:" + isReadOnly);
            logger.info("获得列" + i + "能否出现在where中:" + isSearchable);
        }
    }

    /**
     * 数据库类型,枚举
     */
    public enum DB {
        /**
         * 数据库类型
         */
        ORACLE, MYSQL, SQL_SERVER, SQL_SERVER2005, DB2, INFORMIX, SYBASE, OTHER, EMPTY
    }

    /**
     * 匹配类型
     */
    public enum PATTERN {
        /**
         * 表
         */
        TABLE,
        /**
         * 字段
         */
        COLUMN,
        /**
         * 主键
         */
        PRIMARY_KEY
    }

}
