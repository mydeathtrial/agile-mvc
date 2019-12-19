package com.agile.common.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alibaba.druid.sql.ast.expr.SQLBinaryOperator.Equality;

/**
 * 描述：
 * <p>创建时间：2018/12/6<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class SqlUtil {

    private static final String REPLACE_NULL_CONDITION = " 1=1 ";
    private static final String REPLACE_NULL = "null";
    private static final String NOT_FOUND_PARAM = "@NOT_FOUND_PARAM_";
    private static final String PARAM_START = "@_START_";
    private static final String PARAM_END = "_END_";
    private static final String PARAM_EQUAL = "_EQUAL_";

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
     * @param sql 原sql
     * @return 生成的sql结果
     */
    public static String parserSQL(String sql, Map<String, Object> parameters) {
        sql = sql.replaceAll("(?<!\\\\)\\{", PARAM_START).replaceAll("(?<!\\\\)}", PARAM_END).replace(":-", PARAM_EQUAL);

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        if (statement instanceof SQLSelectStatement) {
            parserSelect((SQLSelectStatement) statement, parameters);
        } else if (statement instanceof SQLUpdateStatement) {
            parserUpdate((SQLUpdateStatement) statement, parameters);
        } else if (statement instanceof SQLDeleteStatement) {
            parserDelete((SQLDeleteStatement) statement, parameters);
        }

        return statement.toString();
    }

    private static SQLObject parserDelete(SQLDeleteStatement statement, Map<String, Object> parameters) {
        SQLTableSource from = statement.getFrom();
        parsingTableSource(from, parameters);

        SQLExpr where = statement.getWhere();
        parserSQLObject(where, parameters);

        return statement;
    }

    private static SQLObject parserUpdate(SQLUpdateStatement statement, Map<String, Object> parameters) {
        List<SQLUpdateSetItem> updateSetItems = statement.getItems();
        parsingUpdateItem(updateSetItems, parameters);

        SQLTableSource from = statement.getFrom();
        parsingTableSource(from, parameters);

        SQLExpr where = statement.getWhere();
        parserSQLObject(where, parameters);

        SQLOrderBy order = statement.getOrderBy();
        if (order != null) {
            parsingOrderItem(order.getItems(), parameters);
        }

        return statement;
    }

    /**
     * 处理查询语句
     *
     * @param statement 查询statement
     * @return 处理后的sql对象
     */
    private static void parserSelect(SQLSelectStatement statement, Map<String, Object> parameters) {
        SQLSelectQuery query = statement.getSelect().getQuery();
        parserQuery(query, parameters);
    }

    private static void parserQuery(SQLSelectQuery query, Map<String, Object> parameters) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectQueryBlock) query);
            List<SQLSelectItem> select = sqlSelectQueryBlock.getSelectList();
            parsingSelectItem(select, parameters);

            SQLTableSource from = sqlSelectQueryBlock.getFrom();
            parsingTableSource(from, parameters);

            SQLExpr where = sqlSelectQueryBlock.getWhere();
            parserSQLObject(where, parameters);

            SQLObject parent = where.getParent();
            if (parent instanceof SQLSelectQueryBlock) {
                where = ((SQLSelectQueryBlock) parent).getWhere();
            }

            sqlSelectQueryBlock.setWhere(parsingWhereConstant(where));
            SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
            if (groupBy != null) {
                parserSQLObject(groupBy, parameters);
            }

            SQLOrderBy order = sqlSelectQueryBlock.getOrderBy();
            if (order != null) {
                parsingOrderItem(order.getItems(), parameters);
            }
        } else if (query instanceof SQLUnionQuery) {
            parserQuery(((SQLUnionQuery) query).getLeft(), parameters);
            parserQuery(((SQLUnionQuery) query).getRight(), parameters);
        }
    }

    private static SQLExpr parsingWhereConstant(SQLExpr sqlExpr) {
        String where = SQLUtils.toSQLString(sqlExpr);
        where = where.replaceAll("((OR|AND|LIKE)[\\s]+1[\\s]*=[\\s]*1)|(1[\\s]*=[\\s]*1[\\s]+(OR|AND|LIKE))|(^1[\\s]*=[\\s]*1)", "").trim();
        if (StringUtil.isBlank(where) || "1 = 1".equals(where)) {
            return null;
        }
        return SQLUtils.toSQLExpr(where);
    }

    /**
     * 处理查询字段
     *
     * @param select 查询字段集合
     */
    private static void parsingSelectItem(List<SQLSelectItem> select, Map<String, Object> parameters) {
        Set<SQLSelectItem> set = select.stream()
                .flatMap(n -> parsingMuchPlaceHolder(n.getExpr(), parameters, SqlUtil::getMuchColumnExprString).stream())
                .map(SQLSelectItem::new)
                .collect(Collectors.toSet());
        select.clear();
        select.addAll(set);
    }

    /**
     * 处理更新字段
     *
     * @param updateSetItems 更新字段集合
     */
    private static void parsingUpdateItem(List<SQLUpdateSetItem> updateSetItems, Map<String, Object> parameters) {

        Set<SQLUpdateSetItem> parsedUpdateSetItems = updateSetItems.stream()
                .peek(n -> {
                    SQLExpr columnExpr = parsingSinglePlaceHolder(n.getColumn(), parameters, SqlUtil::getColumnExprString);
                    n.setColumn(columnExpr);

                    SQLExpr valueExpr = parsingSinglePlaceHolder(n.getValue(), parameters, SqlUtil::getValueExprString);
                    n.setColumn(valueExpr);
                })
                .filter(n -> processed(SQLUtils.toSQLString(n)))
                .collect(Collectors.toSet());

        updateSetItems.clear();
        updateSetItems.addAll(parsedUpdateSetItems);
    }

    /**
     * 处理排序字段
     *
     * @param orderByItems 更新排序字段集合
     */
    private static void parsingOrderItem(List<SQLSelectOrderByItem> orderByItems, Map<String, Object> parameters) {
        Set<SQLSelectOrderByItem> set = orderByItems.stream()
                .flatMap(n -> parsingMuchPlaceHolder(n.getExpr(), parameters, SqlUtil::getMuchColumnExprString).stream())
                .map(SQLSelectOrderByItem::new)
                .collect(Collectors.toSet());
        orderByItems.clear();
        orderByItems.addAll(set);
    }

    private static String getValueExprString(String defaultValue, Object value) {
        if (value != null) {
            return parsingSpecialCharacters(value.toString());
        }
        if (defaultValue != null) {
            return parsingSpecialCharacters(defaultValue);
        }
        return null;
    }

    private static String getColumnExprString(String defaultValue, Object value) {
        if (value != null) {
            SQLExpr sqlExpr = SQLUtils.toSQLExpr(value.toString());
            if (sqlExpr instanceof SQLIdentifierExpr
                    || sqlExpr instanceof SQLIntegerExpr
                    || sqlExpr instanceof SQLCharExpr
                    || sqlExpr instanceof SQLPropertyExpr) {
                return parsingSpecialCharacters(value.toString());
            }
        }
        return parsingSpecialCharacters(defaultValue);
    }

    private static String getMuchColumnExprString(String defaultValue, Object value) {
        if (value != null) {
            Stream<?> stream;
            if (Collection.class.isAssignableFrom(value.getClass())) {
                stream = ((Collection<?>) value).stream();
            } else if (value.getClass().isArray()) {
                ArrayList<Object> collection = new ArrayList<>();
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object v = Array.get(value, i);
                    collection.add(v);
                }
                stream = collection.stream();
            } else {
                stream = Stream.of(value);
            }
            String result = stream.filter(Objects::nonNull)
                    .flatMap(n -> Stream.of(parsingSpecialCharacters(n.toString()).split(",")))
                    .filter(node -> {
                        try {
                            return SQLUtils.toSQLExpr(node) instanceof SQLIntegerExpr
                                    || SQLUtils.toSQLExpr(node) instanceof SQLIdentifierExpr
                                    || SQLUtils.toSQLExpr(node) instanceof SQLCharExpr;
                        } catch (Exception e) {
                            return true;
                        }
                    })
                    .collect(Collectors.joining(","));
            return StringUtil.isBlank(result) ? null : result;
        }
        return defaultValue == null ? null : Stream.of(defaultValue.split(",")).map(SqlUtil::parsingSpecialCharacters).collect(Collectors.joining(","));
    }

    private static String getMuchValueExprString(String defaultValue, Object value) {
        if (value != null) {
            Stream<?> stream;
            if (Collection.class.isAssignableFrom(value.getClass())) {
                stream = ((Collection<?>) value).stream();
            } else if (value.getClass().isArray()) {
                ArrayList<Object> collection = new ArrayList<>();
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object v = Array.get(value, i);
                    collection.add(v);
                }
                stream = collection.stream();
            } else {
                stream = Stream.of(value);
            }
            return stream.filter(Objects::nonNull).map(n -> String.format("'%s'", parsingSpecialCharacters(n.toString()))).collect(Collectors.joining(","));
        }
        return defaultValue == null ? null : Stream.of(defaultValue.split(",")).map(n -> String.format("'%s'", parsingSpecialCharacters(n))).collect(Collectors.joining(","));
    }

    /**
     * 处理多表达式（逗号分割）
     *
     * @param sqlExpr    sql表达式
     * @param parameters 参数集合
     * @return 处理后的sql表达式
     */
    private static List<SQLExpr> parsingMuchPlaceHolder(SQLExpr sqlExpr, Map<String, Object> parameters, BiFunction<String, Object, String> function) {
        String sql = SQLUtils.toMySqlString(sqlExpr);
        if (sql.contains(PARAM_START) && sql.contains(PARAM_END)) {
            Map<String, String> map = StringUtil.getGroupByStartEnd(sql, PARAM_START, PARAM_END, PARAM_EQUAL);
            map.entrySet().forEach(e -> {
                String key = e.getKey();
                String defaultValue = e.getValue();
                Object value = parameters.get(key);

                e.setValue(function.apply(defaultValue, value));
            });
            sql = parsingSqlString(sql, map);

            return Stream.of(sql.split("[\\,]+(?=[^\\)]*(\\(|$))")).filter(n -> processed(n)).map(SQLUtils::toSQLExpr).collect(Collectors.toList());
        }
        return Collections.singletonList(sqlExpr);
    }

    /**
     * 处理单表达式
     *
     * @param sqlExpr    sql表达式
     * @param parameters 参数集合
     * @return 处理后的sql表达式
     */
    private static SQLExpr parsingSinglePlaceHolder(SQLExpr sqlExpr, Map<String, Object> parameters, BiFunction<String, Object, String> function) {
        String sql = SQLUtils.toMySqlString(sqlExpr);
        if (sql.contains(PARAM_START) && sql.contains(PARAM_END)) {
            Map<String, String> map = StringUtil.getGroupByStartEnd(sql, PARAM_START, PARAM_END, PARAM_EQUAL);
            map.entrySet().forEach(e -> {
                String key = e.getKey();
                String defaultValue = e.getValue();
                Object value = parameters.get(key);

                value = function.apply(defaultValue, value);

                String v = value == null ? defaultValue : value.toString();

                e.setValue(v);
            });
            return SQLUtils.toSQLExpr(parsingSqlString(sql, map));
        }
        return sqlExpr;
    }

    /**
     * 处理查询的from部分
     *
     * @param from from部分
     */
    private static void parsingTableSource(SQLTableSource from, Map<String, Object> parameters) {

        if (from instanceof SQLSubqueryTableSource) {
            String childSelect = ((SQLSubqueryTableSource) from).getSelect().toString();
            childSelect = parserSQL(childSelect, parameters);
            SQLStatement childSelectSQLStatement = SQLParserUtils.createSQLStatementParser(childSelect, JdbcUtils.MYSQL).parseStatement();
            ((SQLSubqueryTableSource) from).setSelect(((SQLSelectStatement) childSelectSQLStatement).getSelect());
        } else if (from instanceof SQLJoinTableSource) {
            SQLTableSource left = ((SQLJoinTableSource) from).getLeft();
            parsingTableSource(left, parameters);

            SQLTableSource right = ((SQLJoinTableSource) from).getRight();
            parsingTableSource(right, parameters);
        } else if (from instanceof SQLUnionQueryTableSource) {
            parserQuery(((SQLUnionQueryTableSource) from).getUnion(), parameters);
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
    private static void parserSQLObject(SQLObject sqlObject, Map<String, Object> parameters) {
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
            parsingPart(part, parameters);
        }
        if (sqlObject instanceof SQLSelectGroupByClause) {
            SQLSelectGroupByClause proxy = ((SQLSelectGroupByClause) sqlObject);
            SQLExpr having = proxy.getHaving();

            SQLObject parent = having.getParent();
            if (parent instanceof SQLSelectGroupByClause) {
                having = parsingWhereConstant(((SQLSelectGroupByClause) parent).getHaving());
                proxy.setHaving(having);
            }
        }
    }

    private static void parsingPart(SQLObject part, Map<String, Object> parameters) {
        if (part instanceof SQLInListExpr) {
            parsingInList((SQLInListExpr) part, parameters);
        } else if (part instanceof SQLInSubQueryExpr) {
            parsingInSubQuery((SQLInSubQueryExpr) part, parameters);
        } else if (part instanceof SQLBinaryOpExpr) {
            parsingBinaryOp((SQLBinaryOpExpr) part, parameters);
        } else if (part instanceof SQLPropertyExpr) {
            parsingPart(part.getParent(), parameters);
        } else if (part instanceof SQLMethodInvokeExpr) {
            parsingMethodInvoke((SQLMethodInvokeExpr) part, parameters);
        }
    }

    private static void parsingMethodInvoke(SQLMethodInvokeExpr methodInvokeExpr, Map<String, Object> parameters) {
        String sql = parsingSqlString(SQLUtils.toSQLString(methodInvokeExpr), parameters);
        SQLUtils.replaceInParent(methodInvokeExpr, SQLUtils.toSQLExpr(sql));

        if (processed(sql)) {
            return;
        }
        SQLObject parent = methodInvokeExpr.getParent();
        if (parent instanceof SQLBinaryOpExpr) {
            ((SQLBinaryOpExpr) parent).setRight(SQLUtils.toSQLExpr("1"));
            ((SQLBinaryOpExpr) parent).setLeft(SQLUtils.toSQLExpr("1"));
            ((SQLBinaryOpExpr) parent).setOperator(Equality);
        } else if (parent instanceof SQLInListExpr) {
            ((SQLInListExpr) parent).getTargetList().remove(methodInvokeExpr);
        } else if (parent instanceof SQLOrderBy) {
            ((SQLOrderBy) parent).getItems().remove(methodInvokeExpr);
        } else if (parent instanceof SQLUpdateSetItem) {
            SQLObject updateStatement = parent.getParent();
            if (updateStatement instanceof SQLUpdateStatement) {
                ((SQLUpdateStatement) updateStatement).getItems().remove(parent);
            }
        } else if (parent instanceof SQLSelectItem) {
            SQLObject selectQuery = parent.getParent();
            if (selectQuery instanceof SQLSelectQueryBlock) {
                ((SQLSelectQueryBlock) selectQuery).getSelectList().remove(parent);
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
            if (!processed(sql)) {
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
    private static boolean processed(String sql) {
        return !sql.contains(NOT_FOUND_PARAM);
    }

    /**
     * 处理where info in （select）类型条件
     * o
     *
     * @param c in的druid表达式
     */
    private static void parsingInSubQuery(SQLInSubQueryExpr c, Map<String, Object> parameters) {
        SQLSelect sqlSelect = c.getSubQuery();
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(parserSQL(sqlSelect.toString(), parameters), JdbcUtils.MYSQL);
        sqlSelect.setQuery(((SQLSelectStatement) sqlStatementParser.parseStatement()).getSelect().getQueryBlock());
    }

    /**
     * 处理where info in （list）类型条件
     *
     * @param c in的druid表达式
     */
    private static void parsingInList(SQLInListExpr c, Map<String, Object> parameters) {
        List<SQLExpr> list = c.getTargetList().stream().flatMap(n -> parsingMuchPlaceHolder(n, parameters, SqlUtil::getMuchValueExprString).stream()).collect(Collectors.toList());

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
    private static void parsingBinaryOp(SQLBinaryOpExpr c, Map<String, Object> parameters) {
        String sql = SQLUtils.toMySqlString(c.getRight());
        if (!sql.startsWith("'")) {
            sql = "'" + sql;
        }
        if (!sql.endsWith("'")) {
            sql = sql + "'";
        }
        c.setRight(SQLUtils.toSQLExpr(sql));
        parsingBinaryToString(c.getRight(), parameters);
        boolean isParsing = parser(SQLBinaryOpExpr.split(c));
        if (!isParsing) {
            if (!SQLUtils.replaceInParent(c, null)) {
                SQLUtils.replaceInParent(c, SQLUtils.toSQLExpr(REPLACE_NULL_CONDITION));
            }
        }
    }

    private static void parsingBinaryToString(SQLExpr sqlExpr, Map<String, Object> parameters) {

        if (!(sqlExpr instanceof SQLIntegerExpr) && !(sqlExpr instanceof SQLNullExpr)) {
            SQLUtils.replaceInParent(sqlExpr, parsingSinglePlaceHolder(sqlExpr, parameters, SqlUtil::getValueExprString));
        }
    }

    /**
     * 处理参数占位
     *
     * @param sql    未处理的sql语句
     * @param params 参数集合
     * @return 处理过的sql
     */
    private static String parsingSqlString(String sql, Map<String, ?> params) {
        return StringUtil.parsingPlaceholder(PARAM_START, PARAM_END, PARAM_EQUAL, sql, params, NOT_FOUND_PARAM);
    }

    /**
     * 处理参数特殊字符
     *
     * @param sql 语法段
     */
    private static String parsingSpecialCharacters(String sql) {
        if (sql == null) {
            return null;
        }

        return sql.replace("'", "''");
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
//        String sql = "SELECT a,{column} FROM tableA as ta LEFT JOIN (\n" +
//                "SELECT d,e,f FROM tableB as tb where d = {d:-d} and e like '%{e:-e}' or f in ({f})\n" +
//                ") ON ta.a = tb.d where ta.a = {a} and tb.b like '%{b}' or tb.c in ({c}) or tb.d in (select g from tableC where h in ({h}))\n" +
//                "group by ta.a,ta.b,ta.c HAVING ta.a = {ga} and tb.b like '%{gb}' or tb.c in (select i from tableD where j in({j})) \n" +
//                "ORDER BY {order}";
//
//        String sqlEsCount = "select  date_format(count_date, '{format}') as `key` ,  date_format(count_date, '{format}') as `name` , sum(es_eqpt_count) as `value` " +
//                " from asset_count " +
//                " where date_format(count_date, '{format}') >= date_format({startTime}, '{format}') " +
//                " and date_format(count_date, '{format}') <= date_format({endTime}, '{format}')" +
//                " group by `key`";
//
//        Map<String, Object> map = Maps.newHashMap();
////        map.put("column", "b,c");
////        map.put("a", "3ovf6cDmchXGZHNwNFHVqQsGU1DouofAx7fabVdHKAEs6oY3no AND (SELECT \n" +
////                "3372 FROM (SELECT(SLEEP(5)))gHad)\n");
////        map.put("b", "3ovf6cDmchXGZHNwNFHVqQsGU1DouofAx7fabVdHKAEs6oY3no UNION ALL \n" +
////                "SELECT \n" +
////                "NULL,NULL,CONCAT(0x717a7a7171,0x6e54654e58545a4d4f7574724141664a5647744e\n" +
////                "4f546a455367676b77797a736554496a746b464a,0x717a766a71)—\n" +
////                "VWNm\n");
////        map.put("c", new String[]{"c1", "c2"});
////        map.put("d", "' UNION ALL SELECT NULL,NULL,CONCAT(0x716b627071,0x4373634876736f466545626669726f65614e55714a65765852446e4b72577464664a6f6f5a4b6d51,0x716b717671),NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL-- lhlF");
////        map.put("e", "' AND (SELECT 1470 FROM (SELECT(SLEEP(5)))uxHB) AND \n" +
////                "'NemB'='NemB\n");
////        map.put("f", new String[]{"f1", "f2"});
////        map.put("g", "g");
////        map.put("h", new String[]{"h1", "h2"});
////        map.put("j", new String[]{"j1", "j2"});
////        map.put("ga", "ga");
////        map.put("gb", "gb");
////        map.put("order", "(SELECT (CASE WHEN (6291=6291) THEN 0x2d6173736f6352756c65 ELSE (SELECT 8409 UNION SELECT 5975) END))");
//
////        map.put("format", "%Y/%m/%d");
//
//        parserSQL(sqlEsCount, map);
//    }
}
