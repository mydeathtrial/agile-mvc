package com.agile.common.util;

import com.agile.common.base.Constant;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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
    private static final String NOT_FOUND_PARAM = "@NOT_FOUND_PARAM_";
    private static final String NOT_FOUND_PARAM_LIKE_REGEX = "%[ ]*'#NOT FOUND PARAM#'[ ]*%";

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

    public static String parserSQL(String sql, Map<String, Object> parameters) {
        sql = parsingSqlString(sql, parsingParam(parameters));

//        String[] s = StringUtil.getMatchedString("%[\\S]*%", sql);
//        if (s != null) {
//            for (String x : s) {
//                String t = x.replace(Constant.RegularAbout.UP_COMMA, Constant.RegularAbout.BLANK);
//                sql = sql.replace(x, t);
//            }
//        }
        return parserSQL(sql).replace("\\", "");
    }

    /**
     * 根据给定参数动态生成完成参数占位的sql语句
     *
     * @param sql 原sql
     * @return 生成的sql结果
     */
    private static String parserSQL(String sql) {

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        if (statement instanceof SQLSelectStatement) {
            parserSelect((SQLSelectStatement) statement);
        } else if (statement instanceof SQLUpdateStatement) {
            parserUpdate((SQLUpdateStatement) statement);
        } else if (statement instanceof SQLDeleteStatement) {
            parserDelete((SQLDeleteStatement) statement);
        }

        return statement.toString();
    }

    private static SQLObject parserDelete(SQLDeleteStatement statement) {
        SQLTableSource from = statement.getFrom();
        parsingTableSource(from);

        SQLExpr where = statement.getWhere();
        parserSQLObject(where);

        return statement;
    }

    private static SQLObject parserUpdate(SQLUpdateStatement statement) {
        List<SQLUpdateSetItem> updateSetItems = statement.getItems();
        parsingUpdateItem(updateSetItems);

        SQLTableSource from = statement.getFrom();
        parsingTableSource(from);

        SQLExpr where = statement.getWhere();
        parserSQLObject(where);

        SQLOrderBy order = statement.getOrderBy();
        if (order != null) {
            parsingOrderItem(order.getItems());
        }

        return statement;
    }

    /**
     * 处理查询语句
     *
     * @param statement 查询statement
     * @return 处理后的sql对象
     */
    private static void parserSelect(SQLSelectStatement statement) {
        SQLSelectQuery query = statement.getSelect().getQuery();
        parserQuery(query);
    }

    private static void parserQuery(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectQueryBlock) query);
            List<SQLSelectItem> select = sqlSelectQueryBlock.getSelectList();
            parsingSelectItem(select);

            SQLTableSource from = sqlSelectQueryBlock.getFrom();
            parsingTableSource(from);

            SQLExpr where = sqlSelectQueryBlock.getWhere();
            parserSQLObject(where);


            SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
            if (groupBy != null) {
                parserSQLObject(groupBy);
            }

            SQLOrderBy order = sqlSelectQueryBlock.getOrderBy();
            if (order != null) {
                parsingOrderItem(order.getItems());
            }
        } else if (query instanceof SQLUnionQuery) {
            parserQuery(((SQLUnionQuery) query).getLeft());
            parserQuery(((SQLUnionQuery) query).getRight());
        }
    }

    /**
     * 处理查询字段
     *
     * @param select 查询字段集合
     */
    private static void parsingSelectItem(List<SQLSelectItem> select) {
        select.removeIf(sqlSelectItem -> !parser(Collections.singletonList(sqlSelectItem.getExpr())));
    }

    /**
     * 处理更新字段
     *
     * @param updateSetItems 更新字段集合
     */
    private static void parsingUpdateItem(List<SQLUpdateSetItem> updateSetItems) {
        updateSetItems.removeIf(updateSetItem -> !parser(Collections.singletonList(updateSetItem.getValue())));
        updateSetItems.removeIf(updateSetItem -> !parser(Collections.singletonList(updateSetItem.getColumn())));
    }

    /**
     * 处理排序字段
     *
     * @param orderByItems 更新排序字段集合
     */
    private static void parsingOrderItem(List<SQLSelectOrderByItem> orderByItems) {
        orderByItems.removeIf(orderByItem -> !parser(Collections.singletonList(orderByItem.getExpr())));
        orderByItems.forEach(SqlUtil::parsingOrderItem);
    }

    private static void parsingOrderItem(SQLSelectOrderByItem orderByItem) {
        String sql = SQLUtils.toMySqlString(orderByItem);
        if (sql.startsWith(Constant.RegularAbout.UP_COMMA) && sql.endsWith(Constant.RegularAbout.UP_COMMA)) {
            orderByItem.setExpr(SQLUtils.toSQLExpr(sql.substring(Constant.NumberAbout.ONE, sql.length() - Constant.NumberAbout.TWO)));
        }
    }

    /**
     * 处理查询的from部分
     *
     * @param from from部分
     */
    private static void parsingTableSource(SQLTableSource from) {
        if (from instanceof SQLSubqueryTableSource) {
            String childSelect = ((SQLSubqueryTableSource) from).getSelect().toString();
            childSelect = parserSQL(childSelect);
            SQLStatement childSelectSQLStatement = SQLParserUtils.createSQLStatementParser(childSelect, JdbcUtils.MYSQL).parseStatement();
            ((SQLSubqueryTableSource) from).setSelect(((SQLSelectStatement) childSelectSQLStatement).getSelect());
        } else if (from instanceof SQLJoinTableSource) {
            SQLTableSource left = ((SQLJoinTableSource) from).getLeft();
            parsingTableSource(left);

            SQLTableSource right = ((SQLJoinTableSource) from).getRight();
            parsingTableSource(right);
        } else if (from instanceof SQLUnionQueryTableSource) {
            parserQuery(((SQLUnionQueryTableSource) from).getUnion());
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
     * @param sqlObject sql druid对象
     */
    private static void parserSQLObject(SQLObject sqlObject) {
        if (sqlObject == null) {
            return;
        }
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
                parsingInList((SQLInListExpr) part);
            } else if (part instanceof SQLInSubQueryExpr) {
                parsingInSubQuery((SQLInSubQueryExpr) part);
            } else if (part instanceof SQLBinaryOpExpr) {
                parsingBinaryOp((SQLBinaryOpExpr) part);
            }
        }
    }

    /**
     * 直接处理sqlExpr中的占位参数，不符合的直接踢除
     *
     * @param items sqlExpr集合
     * @return 返回是否处理成功
     */
    private static boolean parser(List<SQLExpr> items) {
        for (SQLExpr item : items) {
            String sql = SQLUtils.toMySqlString(item);
            if (!unprocessed(sql)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查sql语句是否存在参数占位
     *
     * @param sql sql语句
     * @return 是否
     */
    private static boolean unprocessed(String sql) {
        return !sql.contains(NOT_FOUND_PARAM);
    }

    /**
     * 处理where info in （select）类型条件
     *
     * @param c in的druid表达式
     */
    private static void parsingInSubQuery(SQLInSubQueryExpr c) {
        SQLSelect sqlSelect = c.getSubQuery();
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(parserSQL(sqlSelect.toString()), JdbcUtils.MYSQL);
        sqlSelect.setQuery(((SQLSelectStatement) sqlStatementParser.parseStatement()).getSelect().getQueryBlock());
    }

    /**
     * 处理where info in （list）类型条件
     *
     * @param c in的druid表达式
     */
    private static void parsingInList(SQLInListExpr c) {
        List<SQLExpr> items = c.getTargetList();
        if (items == null) {
            return;
        }
        List<SQLExpr> list = new ArrayList<>();
        for (SQLExpr item : items) {
            String sql = SQLUtils.toMySqlString(item);
            if (unprocessed(sql)) {
                list.add(item);
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
     * @param c where表达式段
     */
    private static void parsingBinaryOp(SQLBinaryOpExpr c) {
        parsingBinaryToString(c.getRight());
        boolean isParsing = parser(SQLBinaryOpExpr.split(c));
        if (!isParsing) {
            if (!SQLUtils.replaceInParent(c, null)) {
                SQLUtils.replaceInParent(c, SQLUtils.toSQLExpr(REPLACE_NULL_CONDITION));
            }
        }
    }

    private static void parsingBinaryToString(SQLExpr sqlExpr) {
        if (!(sqlExpr instanceof SQLIntegerExpr)) {
            String cache = SQLUtils.toMySqlString(sqlExpr);
            if (!cache.startsWith(Constant.RegularAbout.UP_COMMA) || !cache.endsWith(Constant.RegularAbout.UP_COMMA)) {
                SQLExpr newSQLExpr = SQLUtils.toSQLExpr(String.format("'%s'", cache));
                SQLUtils.replaceInParent(sqlExpr, newSQLExpr);
            }
        }
    }

    /**
     * 处理参数占位
     *
     * @param sql    未处理的sql语句
     * @param params 参数集合
     * @return 处理过的sql
     */
    private static String parsingSqlString(String sql, Map<String, Object> params) {
        return StringUtil.parsingPlaceholder("{", "}", sql, params, NOT_FOUND_PARAM);
    }

    /**
     * 处理参数集合
     *
     * @param params 参数集合
     */
    private static Map<String, Object> parsingParam(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(params.size());
        for (Map.Entry<String, Object> entity : params.entrySet()) {
            Object value = entity.getValue();
            String sqlValue;
            if (value == null || "".equals(value)) {
                continue;
            }
            if (value.getClass().isArray()) {
                if (((Object[]) value).length == 0) {
                    sqlValue = null;
                } else {
                    List<String> s = Arrays.stream((Object[]) value).map(x -> String.format("'%s'", x)).collect(toList());
                    sqlValue = StringUtil.join(s, ",");
                }
            } else if (value instanceof Collection) {
                Collection<Object> objects = (Collection<Object>) value;
                if (objects.size() == 0) {
                    sqlValue = null;
                } else {
                    Object collection = objects.stream().map(x -> String.format("'%s'", x)).collect(toList());
                    sqlValue = StringUtil.join((Collection) collection, ",");
                }
            } else {
                if (String.valueOf(value).trim().length() == 0) {
                    sqlValue = null;
                } else {
                    sqlValue = String.format("'%s'", String.valueOf(value));
                }

            }
            map.put(entity.getKey(), sqlValue);
        }
        return map;
    }

    /**
     * 查询语句获取排序字段集合
     *
     * @return 排序集合
     */
    public static List<Sort.Order> getSort(String sql) {
        List<Sort.Order> sorts = new ArrayList<>();
        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();
        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        if (sqlSelectQueryBlock == null) {
            return sorts;
        }

        SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
        if (orderBy != null) {
            List<SQLSelectOrderByItem> items = orderBy.getItems();
            if (items != null) {
                for (SQLSelectOrderByItem item : items) {
                    String column = item.getExpr().toString();
                    if (item.getType() == null) {
                        sorts.add(Sort.Order.by(column));
                    } else {
                        Sort.Direction des = Sort.Direction.fromString(item.getType().name_lcase);
                        switch (des) {
                            case ASC:
                                sorts.add(Sort.Order.asc(column));
                                break;
                            case DESC:
                                sorts.add(Sort.Order.desc(column));
                                break;
                            default:
                        }
                    }
                }
            }
        }
        return sorts;
    }

    public static String extract(String sql) {

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);


        String tableName = null;
        if (statement instanceof SQLUpdateStatement) {
            tableName = extractUpdate((SQLUpdateStatement) statement);
        } else if (statement instanceof SQLDeleteStatement) {
            tableName = extractDelete((SQLDeleteStatement) statement);
        } else if (statement instanceof SQLInsertStatement) {
            tableName = extractInsert((SQLInsertStatement) statement);
        }

        return tableName;
    }

    private static String extractUpdate(SQLUpdateStatement statement) {
        return statement.getTableName().getSimpleName();
    }

    private static String extractDelete(SQLDeleteStatement statement) {
        return statement.getTableName().getSimpleName();
    }

    private static String extractInsert(SQLInsertStatement statement) {
        return statement.getTableName().getSimpleName();
    }

//    public static void main(String[] args) {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("taskType", new String[]{"1", "123"});
//        map.put("ids2", new ArrayList<String>() {
//        });
//        map.put("ids3", new HashSet() {{
//            add("asd");
//            add("31");
//        }});
//        map.put("name", new Long(111111123));
//        map.put("taskName1", "qwe");
//        map.put("nameListStatic", "\\{名单2#2\\}");
////        map.put("nameListDong", "#名单2\\}");
//        map.put("order", "column1");
////        String sql = "select name from handle_static where receive_user like '%{nameListStatic}%' and id in ({ids3}) or id = {name} or id = {taskName1} " +
////                " or send_content like '% a {nameListStatic}%' or receive_user like '% a {nameListDong}' or send_content like '%{nameListDong}%' order by {order}";
////        map.put("aaa", "sys_users_id");
////        map.put("bbb", "name");
////        map.put("ccc", "tutors");
////        SqlUtil.parserSQL("select * from sys_users where sys_users_id in ({ids})", map);
////        parserSQL("select * from sys_users where sys_users_id in ({ids}) and name = {name}", map);
////        parserSQL("\tSELECT {aaa},{bbb1}\n" +
////                "FROM sys_users\n" +
////                " GROUP BY sys_users_id,name HAVING sys_users_id in ({ids12}) ", map);
////        String sql = parserSQL("SELECT\n" +
////                "\t`user`.*,\n" +
////                "\tde.depart_name AS sys_depart,\n" +
////                "\tt.*\n" +
////                "FROM\n" +
////                "\tsys_users AS USER,\n" +
////                "\tsys_department AS de,\n" +
////                "\t(\n" +
////                "\t\tSELECT\n" +
////                "\t\t\tGROUP_CONCAT(ROLE_NAME) AS sys_role\n" +
////                "\t\tFROM\n" +
////                "\t\t\tsys_roles\n" +
////                "\t\tWHERE\n" +
////                "\t\t\tSYS_ROLES_ID IN (\n" +
////                "\t\t\t\tSELECT\n" +
////                "\t\t\t\t\tROLE_ID\n" +
////                "\t\t\t\tFROM\n" +
////                "\t\t\t\t\tsys_bt_users_roles\n" +
////                "\t\t\t\tWHERE\n" +
////                "\t\t\t\t\tUSER_ID = {id}\n" +
////                "\t\t\t)\n" +
////                "\t) AS t\n" +
////                "WHERE\n" +
////                "\t`user`.sys_users_id = '%{id32}%'\n" +
////                "AND `user`.sys_depart_id = de.sys_depart_id", map);
//
////        String sql = "select * from datasource_individual where field like '%{condition}%' or name like '%{condition}%' order by field ";
//
////        String sql = "delete sys_users where id like ' %{name}{id}% ' and da like ' %{name}{id}% ' ";
//
//        map.put("assetIp", "192.168.200.106");
//        map.put("datasourceName", "时间");
//        map.put("field", "");
//        System.out.println(parserSQL("select * from vb_analysis where asset_ip ={assetIp} AND flag = 2 AND attack_time >= {startDtm} AND attack_time <= {endDtm} and id in ({ids3})", map) + "\r\r");
////        System.out.println(parserCountSQL(sql, null));
//    }


}
