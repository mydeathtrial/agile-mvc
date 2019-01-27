package com.agile.common.util;


import com.agile.common.base.Constant;
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
import java.util.LinkedList;
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

    private static ResultSet getResultSet(PATTERN type, DBInfo dbInfo, String pattern) {

        String url = createDBUrl(dbInfo);
        try {
            conn = getConnection(url, dbInfo.user, dbInfo.pass);
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

                    switch (dbInfo.type) {
                        case ORACLE:
                            schemaPattern = dbInfo.user;
                            if (null != schemaPattern) {
                                schemaPattern = schemaPattern.toUpperCase();
                            }
                            rs = meta.getTables(null, schemaPattern, pattern, types);
                            break;
                        case MYSQL:
                            schemaPattern = dbInfo.dbName;
                            rs = meta.getTables(schemaPattern, schemaPattern, pattern, types);
                            break;
                        case SQL_SERVER:
                            rs = meta.getTables(null, null, pattern, types);
                            break;
                        case SQL_SERVER2005:
                            rs = meta.getTables(null, null, pattern, types);
                            break;
                        case DB2:
                            schemaPattern = "jence_user";
                            rs = meta.getTables(null, schemaPattern, pattern, types);
                            break;
                        case INFORMIX:
                            rs = meta.getTables(null, null, pattern, types);
                            break;
                        case SYBASE:
                            rs = meta.getTables(null, null, pattern, types);
                            break;
                        default:
                            throw new RuntimeException("不支持的数据库类型");
                    }
                    break;
                case COLUMN:
                    pattern = pattern.toUpperCase();
                    String columnNamePattern = null;
                    if (DB.ORACLE == dbInfo.type) {
                        schemaPattern = dbInfo.user;
                        if (null != schemaPattern) {
                            schemaPattern = schemaPattern.toUpperCase();
                        }
                    }

                    rs = meta.getColumns(null, schemaPattern, pattern, columnNamePattern);
                    break;
                case PRIMARY_KEY:
                    pattern = pattern.toUpperCase();
                    if (DB.ORACLE == dbInfo.type) {
                        schemaPattern = dbInfo.user;
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

    public static List<Map<String, Object>> listTables(String dbType, String url, String username, String password, String tableName) {
        LinkedList<String> list = parseDBUrl(parseDB(dbType), url);
        final int zero = 0;
        final int one = 1;
        final int two = 2;
        DBInfo dbInfo = new DBInfo(dbType, list.get(zero), list.get(one), list.get(two), username, password);

        return listTables(dbInfo, tableName);
    }

    /**
     * 列出数据库的所有表
     */
    public static List<Map<String, Object>> listTables(DBInfo dbInfo, String tableName) {
        if (tableName.contains(Constant.RegularAbout.COMMA)) {
            String[] tables = tableName.replaceAll("((?![%-])\\W)+", Constant.RegularAbout.COMMA).split(Constant.RegularAbout.COMMA);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < tables.length; i++) {
                list.addAll(getDBInfo(PATTERN.TABLE, dbInfo, tables[i]));
            }
            return list;
        }
        return getDBInfo(PATTERN.TABLE, dbInfo, tableName.trim());
    }

    /**
     * 列出表的所有字段
     */
    public static List<Map<String, Object>> listColumns(DBInfo dbInfo, String tableName) {
        List<Map<String, Object>> list = getDBInfo(PATTERN.COLUMN, dbInfo, tableName);
        List<Map<String, Object>> keyList = listPrimayKeys(dbInfo, tableName);
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
    public static List<Map<String, Object>> listPrimayKeys(DBInfo dbInfo, String tableName) {
        return getDBInfo(PATTERN.PRIMARY_KEY, dbInfo, tableName);
    }

    /**
     * 列出表的所有主键
     */
    public static List<Map<String, Object>> getDBInfo(PATTERN pattern, DBInfo dbInfo, String tableName) {
        List<Map<String, Object>> list = null;
        ResultSet rs = null;
        try {
            rs = getResultSet(pattern, dbInfo, tableName);
            list = parseResultSetToMapList(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(conn);
        }
        return list;
    }

    private static LinkedList<String> parseDBUrl(DB dbtype, String url) {
        String temp;
        if (DB.ORACLE.equals(dbtype)) {
            temp = "jdbc:oracle:thin:@([0-9.]+):([0-9]+):([\\w\\W]+)";
        } else if (DB.MYSQL.equals(dbtype)) {
            temp = "jdbc:mysql://([0-9.]+):([0-9]+)/([a-zA-Z0-9_-]+)(?:|\\?)";
        } else if (DB.SQL_SERVER.equals(dbtype)) {
            temp = "jdbc:jtds:sqlserver://([0-9.]+):([0-9]+)/([a-zA-Z0-9_-]+)([\\w\\W=]+)";
        } else if (DB.SQL_SERVER2005.equals(dbtype)) {
            temp = "jdbc:sqlserver://([0-9.]+):([0-9]+);DatabaseName=([a-zA-Z0-9_-]+)";
        } else if (DB.DB2.equals(dbtype)) {
            temp = "jdbc:db2://([0-9.]+):([0-9]+)/([a-zA-Z0-9_-]+)";
        } else if (DB.INFORMIX.equals(dbtype)) {
            temp = "jdbc:informix-sqli://([0-9.]+):([0-9]+)/([a-zA-Z0-9_-]+)";
        } else if (DB.SYBASE.equals(dbtype)) {
            temp = "jdbc:sybase:Tds:([0-9.]+):([0-9]+)/([a-zA-Z0-9_-]+)";
        } else {
            throw new RuntimeException("不认识的数据库类型!");
        }

        return StringUtil.getGroupString(temp, url.replace(" ", ""));
    }

    /**
     * 根据IP,端口,以及数据库名字,拼接Oracle连接字符串
     */
    private static String createDBUrl(DBInfo dbInfo) {
        String template;
        switch (dbInfo.type) {
            case ORACLE:
                template = "jdbc:oracle:thin:@%s:%s:%s";
                break;
            case MYSQL:
                template = "jdbc:mysql://%s:%s/%s";
                break;
            case SQL_SERVER:
                template = "jdbc:jtds:sqlserver://%s:%s/%s";
                break;
            case SQL_SERVER2005:
                template = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
                break;
            case DB2:
                template = "jdbc:db2://%s:%s/%s";
                break;
            case INFORMIX:
                template = "jdbc:informix-sqli://%s:%s/%s";
                break;
            case SYBASE:
                template = "jdbc:sybase:Tds:%s:%s/%s";
                break;
            default:
                throw new RuntimeException("不认识的数据库类型!");
        }
        String url = String.format(template, dbInfo.ip, dbInfo.port, dbInfo.dbName);
        switch (dbInfo.type) {
            case MYSQL:
                url = url + "?serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&allowPublicKeyRetrieval=true";
                break;
            case SQL_SERVER:
                url = url + ";lastupdatecount=true";
                break;
            default:
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

    public static boolean tryLink(DBInfo dbInfo) {
        String url = createDBUrl(dbInfo);
        Connection connection = null;
        try {
            connection = getConnection(url, dbInfo.user, dbInfo.pass);
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
     * 数据库信息
     */
    public static class DBInfo {
        private DB type;
        private String ip;
        private String port;
        private String dbName;
        private String user;
        private String pass;

        public DBInfo(String type, String ip, String port, String dbName, String user, String pass) {
            this.type = parseDB(type);
            this.ip = ip;
            this.port = port;
            this.dbName = dbName;
            this.user = user;
            this.pass = pass;
        }

        public DB getType() {
            return type;
        }

        public String getIp() {
            return ip;
        }

        public String getPort() {
            return port;
        }

        public String getDbName() {
            return dbName;
        }

        public String getUser() {
            return user;
        }

        public String getPass() {
            return pass;
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
