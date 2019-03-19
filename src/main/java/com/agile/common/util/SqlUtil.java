package com.agile.common.util;

import com.agile.common.base.Constant;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * <p>创建时间：2018/12/6<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class SqlUtil {
    private static final String CURLY_BRACES_LEFT = "'\\W*\\{";
    private static final String CURLY_BRACES_RIGHT = "}\\W*'";
    private static final String CURLY_BRACES_LEFT_2 = "'{";
    private static final String CURLY_BRACES_RIGHT_2 = "}'";
    private static final String CURLY_BRACES_LEFT_3 = "{";
    private static final String CURLY_BRACES_RIGHT_3 = "}";
    private static final String PARAM_FORMAT = "{%s}";
    private static final String STRING_PARAM_FORMAT = "'%s'";
    private static final String REPLACE_NULL_CONDITION = " 1=1 ";
    private static final String REPLACE_NULL = "null";
    private static final String REPLACE_COUNT = "count(1)";

    /**
     * 根据给定参数动态生成完成参数占位的查询条数sql语句
     *
     * @param sql        原sql模板
     * @param parameters map格式的sql语句中的参数集合，使用{paramName}方式占位
     * @return 生成的sql结果
     */
    public static String parserCountSQL(String sql, Map<String, Object> parameters) {
        sql = parserSQL(sql, parameters);

        return String.format("select count(1) from (%s) _select_table", sql);
    }

    public static String parserCountSQL(String sql) {
        return parserCountSQL(sql, null);
    }

    /**
     * 根据给定参数动态生成完成参数占位的sql语句
     *
     * @param sql        原sql
     * @param parameters map格式的sql语句中的参数集合，使用{paramName}方式占位
     * @return 生成的sql结果
     */
    public static String parserSQL(String sql, Map<String, Object> parameters) {
        if (!sql.contains(CURLY_BRACES_LEFT_3)) {
            return sql;
        }
        sql = sql.replaceAll(CURLY_BRACES_LEFT, CURLY_BRACES_LEFT_3)
                .replaceAll(CURLY_BRACES_RIGHT, CURLY_BRACES_RIGHT_3)
                .replace(CURLY_BRACES_LEFT_3, CURLY_BRACES_LEFT_2)
                .replace(CURLY_BRACES_RIGHT_3, CURLY_BRACES_RIGHT_2);

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        List<SQLSelectItem> select = sqlSelectQueryBlock.getSelectList();
        parsingSelectItem(select, parameters);

        SQLTableSource from = sqlSelectQueryBlock.getFrom();
        parsingTableSource(from, parameters);

        SQLExpr where = sqlSelectQueryBlock.getWhere();
        if (where != null) {
            parserSQLObject(where, parameters);
        }


        SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
        if (groupBy != null) {
            parserSQLObject(groupBy, parameters);
        }
        return sqlSelectQueryBlock.toString();
    }

    /**
     * 处理查询字段
     *
     * @param select     查询字段集合
     * @param parameters 参数集合
     */
    private static void parsingSelectItem(List<SQLSelectItem> select, Map<String, Object> parameters) {
        select.removeIf(sqlSelectItem -> !parser(Collections.singletonList(sqlSelectItem.getExpr()), parameters));
    }

    /**
     * 处理查询的from部分
     *
     * @param from       from部分
     * @param parameters 参数集合
     */
    private static void parsingTableSource(SQLTableSource from, Map<String, Object> parameters) {
        if (from instanceof SQLSubqueryTableSource) {
            String s = ((SQLSubqueryTableSource) from).getSelect().toString();
            String l = parserSQL(s.replace(CURLY_BRACES_LEFT_2, CURLY_BRACES_LEFT_3).replace(CURLY_BRACES_RIGHT_2, CURLY_BRACES_RIGHT_3), parameters);
            SQLStatement se = SQLParserUtils.createSQLStatementParser(l, JdbcUtils.MYSQL).parseStatement();
            ((SQLSubqueryTableSource) from).setSelect(((SQLSelectStatement) se).getSelect());
        } else if (from instanceof SQLJoinTableSource) {
            SQLTableSource left = ((SQLJoinTableSource) from).getLeft();
            parsingTableSource(left, parameters);

            SQLTableSource right = ((SQLJoinTableSource) from).getRight();
            parsingTableSource(right, parameters);
        }
    }

    /**
     * sql分段，比如把where条件按照表达式拆分成段
     *
     * @param sqlObject sql druid对象
     */
    private static List<SQLObject> getMuchPart(SQLObject sqlObject) {
        if (sqlObject == null) {
            return null;
        }
        List<SQLObject> result = new LinkedList<>();
        List<SQLObject> children = ((SQLExpr) sqlObject).getChildren();
        if (children != null && children.size() > 0) {
            for (SQLObject child : children) {
                if (child instanceof SQLExpr) {
                    List<SQLObject> grandson = ((SQLExpr) child).getChildren();
                    if (grandson == null || grandson.size() == 0) {
                        result.add(sqlObject);
                        break;
                    } else {
                        result.addAll(getMuchPart(child));
                    }
                }
            }
        } else {
            return getMuchPart(sqlObject.getParent());
        }
        return result;
    }

    /**
     * 处理sqlObject直接转转换占位符
     *
     * @param sqlObject  sql druid对象
     * @param parameters 参数集合
     */
    private static void parserSQLObject(SQLObject sqlObject, Map<String, Object> parameters) {
        List<SQLObject> sqlPartInfo = null;
        if (sqlObject instanceof SQLExpr) {
            sqlPartInfo = getMuchPart(sqlObject);
        } else if (sqlObject instanceof SQLSelectGroupByClause) {
            SQLSelectGroupByClause proxy = ((SQLSelectGroupByClause) sqlObject);
            sqlPartInfo = getMuchPart(proxy.getHaving());
        }
        if (sqlPartInfo == null) {
            return;
        }
        for (SQLObject part : sqlPartInfo) {
            if (part instanceof SQLInListExpr) {
                parsingInList((SQLInListExpr) part, parameters);
            } else if (part instanceof SQLInSubQueryExpr) {
                parsingInSubQuery((SQLInSubQueryExpr) part, parameters);
            } else if (part instanceof SQLBinaryOpExpr) {
                parsingBinaryOp((SQLBinaryOpExpr) part, parameters);
            }
        }
    }

    /**
     * 直接处理sqlExpr中的占位参数，不符合的直接踢除
     *
     * @param items      sqlExpr集合
     * @param parameters 参数结集合
     * @return 返回是否处理成功
     */
    private static boolean parser(List<SQLExpr> items, Map<String, Object> parameters) {
        for (SQLExpr item : items) {
            String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM, item.toString(), 0);
            if (key != null) {
                Object value = parameters.get(key);
                if (parameters.get(key) == null || StringUtil.isEmpty(String.valueOf(value))) {
                    return false;
                } else {
                    String format = item.toString().replace(String.format(PARAM_FORMAT, key), PARAM_FORMAT);
                    Object param = parameters.get(key);
                    SQLUtils.replaceInParent(item, SQLUtils.toSQLExpr(format.replace(PARAM_FORMAT, param.toString())));
                }
            }
        }
        return true;
    }

    /**
     * 处理where info in （select）类型条件
     *
     * @param c          in的druid表达式
     * @param parameters 参数集合
     */
    private static void parsingInSubQuery(SQLInSubQueryExpr c, Map<String, Object> parameters) {
        SQLSelect sqlSelect = c.getSubQuery();
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(parserSQL(sqlSelect.toString(), parameters), JdbcUtils.MYSQL);
        sqlSelect.setQuery(((SQLSelectStatement) sqlStatementParser.parseStatement()).getSelect().getQueryBlock());
    }

    /**
     * 处理where info in （list）类型条件
     *
     * @param c          in的druid表达式
     * @param parameters 参数集合
     */
    private static void parsingInList(SQLInListExpr c, Map<String, Object> parameters) {
        List<SQLExpr> items = c.getTargetList();
        if (items == null) {
            return;
        }
        List<SQLExpr> list = new ArrayList<>();
        for (SQLExpr item : items) {
            String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM, item.toString(), 0);
            if (key != null) {
                Object value = parameters.get(key);
                if (parameters.get(key) != null && !StringUtil.isEmpty(String.valueOf(value))) {
                    Object param = parameters.get(key);
                    if (param instanceof Iterable) {
                        Iterator it = ((Iterable) param).iterator();
                        while (it.hasNext()) {
                            list.add(SQLUtils.toSQLExpr(String.format(STRING_PARAM_FORMAT, String.valueOf(it.next()))));
                        }
                    } else if (param.getClass().isArray()) {
                        for (Object o : (Object[]) param) {
                            list.add(SQLUtils.toSQLExpr(String.format(STRING_PARAM_FORMAT, String.valueOf(o))));
                        }
                    }
                }
            }
        }
        if (list.size() > 0) {
            c.setTargetList(list);
        } else {
            if (!(c.getParent() instanceof SQLReplaceable)) {
                c.setNot(!c.isNot());
                c.setTargetList(Collections.singletonList(SQLUtils.toSQLExpr(REPLACE_NULL)));
            } else {
                SQLUtils.replaceInParent(c, SQLUtils.toSQLExpr(REPLACE_NULL_CONDITION));
            }

        }
    }

    /**
     * 处理普通where表达式
     *
     * @param c          where表达式段
     * @param parameters 参数集合
     */
    private static void parsingBinaryOp(SQLBinaryOpExpr c, Map<String, Object> parameters) {
        boolean isParsing = parser(SQLBinaryOpExpr.split(c), parameters);
        if (!isParsing) {
            if (!SQLUtils.replaceInParent(c, null)) {
                SQLUtils.replaceInParent(c, SQLUtils.toSQLExpr(REPLACE_NULL_CONDITION));
            }
        }
    }


//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("ids", new String[]{"1", "123"});
//        map.put("name", "222");
//        map.put("id", "111");
////        map.put("aaa", "sys_users_id");
////        map.put("bbb", "name");
////        map.put("ccc", "tutors");
////        SqlUtil.parserSQL("select * from sys_users where sys_users_id in ({ids})", map);
////        parserSQL("select * from sys_users where sys_users_id in ({ids}) and name = {name}", map);
////        parserSQL("\tSELECT {aaa},{bbb1}\n" +
////                "FROM sys_users\n" +
////                " GROUP BY sys_users_id,name HAVING sys_users_id in ({ids12}) ", map);
//        String sql = parserSQL("SELECT\n" +
//                "\t`user`.*,\n" +
//                "\tde.depart_name AS sys_depart,\n" +
//                "\tt.*\n" +
//                "FROM\n" +
//                "\tsys_users AS USER,\n" +
//                "\tsys_department AS de,\n" +
//                "\t(\n" +
//                "\t\tSELECT\n" +
//                "\t\t\tGROUP_CONCAT(ROLE_NAME) AS sys_role\n" +
//                "\t\tFROM\n" +
//                "\t\t\tsys_roles\n" +
//                "\t\tWHERE\n" +
//                "\t\t\tSYS_ROLES_ID IN (\n" +
//                "\t\t\t\tSELECT\n" +
//                "\t\t\t\t\tROLE_ID\n" +
//                "\t\t\t\tFROM\n" +
//                "\t\t\t\t\tsys_bt_users_roles\n" +
//                "\t\t\t\tWHERE\n" +
//                "\t\t\t\t\tUSER_ID = '{id}'\n" +
//                "\t\t\t)\n" +
//                "\t) AS t\n" +
//                "WHERE\n" +
//                "\t`user`.sys_users_id = '%{id}%'\n" +
//                "AND `user`.sys_depart_id = de.sys_depart_id", map);
//        System.out.println(sql);
//    }
}
