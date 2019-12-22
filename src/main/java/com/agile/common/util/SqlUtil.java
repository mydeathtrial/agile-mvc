package com.agile.common.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    /**
     * 常量表达式正则
     */
    public static final String CONSTANT_CONDITION_REGEX = "((OR|AND|LIKE)[\\s]+1[\\s]*=[\\s]*1)|(1[\\s]*=[\\s]*1[\\s]+(OR|AND|LIKE))|(^1[\\s]*=[\\s]*1)";
    /**
     * 常量表达式
     */
    public static final String CONSTANT_CONDITION = "1 = 1";

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
        sql = Param.parsingSqlString(sql, Param.parsingParam(parameters));
        return parserSQL(sql);
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

    private static void parserDelete(SQLDeleteStatement statement) {
        SQLTableSource from = statement.getFrom();
        parsingTableSource(from);

        parsingWhere(statement);
    }

    private static void parserUpdate(SQLUpdateStatement statement) {
        Param.parsingSQLUpdateStatement(statement);

        SQLTableSource from = statement.getFrom();
        parsingTableSource(from);

        parsingWhere(statement);

        SQLOrderBy order = statement.getOrderBy();
        Param.parsingSQLOrderBy(order);
    }

    /**
     * 处理查询语句
     *
     * @param statement 查询statement
     */
    private static void parserSelect(SQLSelectStatement statement) {
        SQLSelectQuery query = statement.getSelect().getQuery();
        parserQuery(query);
    }

    private static void parserQuery(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectQueryBlock) query);
            Param.parsingSQLSelectItem(sqlSelectQueryBlock);

            SQLTableSource from = sqlSelectQueryBlock.getFrom();
            parsingTableSource(from);

            parsingWhere(sqlSelectQueryBlock);

            SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
            if (groupBy != null) {
                parserSQLObject(groupBy);
            }

            SQLOrderBy order = sqlSelectQueryBlock.getOrderBy();
            if (order != null) {
                Param.parsingSQLOrderBy(order);
            }
        } else if (query instanceof SQLUnionQuery) {
            parserQuery(((SQLUnionQuery) query).getLeft());
            parserQuery(((SQLUnionQuery) query).getRight());
        }
    }

    /**
     * 处理where条件
     *
     * @param expr where的父级表达式
     * @param <T>  泛型
     */
    private static <T> void parsingWhere(T expr) {
        SQLExpr where = null;
        if (expr instanceof SQLSelectQueryBlock) {
            where = ((SQLSelectQueryBlock) expr).getWhere();
        } else if (expr instanceof SQLUpdateStatement) {
            where = ((SQLUpdateStatement) expr).getWhere();
        } else if (expr instanceof SQLDeleteStatement) {
            where = ((SQLDeleteStatement) expr).getWhere();
        }
        if (where == null) {
            return;
        }
        parserSQLObject(where);

        SQLObject parent = where.getParent();

        if (expr instanceof SQLSelectQueryBlock) {
            where = ((SQLSelectQueryBlock) parent).getWhere();
            SQLExpr newWhere = parsingWhereConstant(where);
            ((SQLSelectQueryBlock) expr).setWhere(newWhere);
        } else if (expr instanceof SQLUpdateStatement) {
            where = ((SQLUpdateStatement) parent).getWhere();
            SQLExpr newWhere = parsingWhereConstant(where);
            ((SQLUpdateStatement) expr).setWhere(newWhere);
        } else {
            where = ((SQLDeleteStatement) parent).getWhere();
            SQLExpr newWhere = parsingWhereConstant(where);
            ((SQLDeleteStatement) expr).setWhere(newWhere);
        }
    }

    private static SQLExpr parsingWhereConstant(SQLExpr sqlExpr) {
        String where = SQLUtils.toSQLString(sqlExpr);
        where = where.replaceAll(CONSTANT_CONDITION_REGEX, "").trim();
        if (StringUtil.isBlank(where) || CONSTANT_CONDITION.equals(where)) {
            return null;
        }
        return SQLUtils.toSQLExpr(where);
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
            Param.parsingSQLSelectGroupByClause(proxy);
            sqlPartInfo = getMuchPart(proxy.getHaving());
        }
        if (sqlPartInfo == null) {
            return;
        }
        for (SQLObject part : sqlPartInfo) {
            parsingPart(part);
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

    private static void parsingPart(SQLObject part) {
        if (part instanceof SQLInListExpr) {
            Param.parsingSQLInListExpr((SQLInListExpr) part);
        } else if (part instanceof SQLInSubQueryExpr) {
            parsingInSubQuery((SQLInSubQueryExpr) part);
        } else if (part instanceof SQLBinaryOpExpr) {
            Param.parsingSQLBinaryOpExpr((SQLBinaryOpExpr) part);
        } else if (part instanceof SQLPropertyExpr) {
            parsingPart(part.getParent());
        } else if (part instanceof SQLMethodInvokeExpr) {
            parsingMethodInvoke((SQLMethodInvokeExpr) part);
        }
    }

    private static void parsingMethodInvoke(SQLMethodInvokeExpr methodInvokeExpr) {
        if (!Param.unprocessed(methodInvokeExpr)) {
            return;
        }
        SQLObject parent = methodInvokeExpr.getParent();
        if (parent instanceof SQLBinaryOpExpr) {
            ((SQLBinaryOpExpr) parent).setRight(SQLUtils.toSQLExpr("1"));
            ((SQLBinaryOpExpr) parent).setLeft(SQLUtils.toSQLExpr("1"));
            ((SQLBinaryOpExpr) parent).setOperator(Equality);
        } else if (parent instanceof SQLInListExpr) {
            ((SQLInListExpr) parent).getTargetList().remove(methodInvokeExpr);
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

    /**
     * 提取操作的sql语句中包含的目标表表名
     *
     * @param sql sql语句
     * @return 表名字
     */
    public static String extractTableName(String sql) {

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
//        String sql = "SELECT a,{column} as tt FROM tableA as ta LEFT JOIN (\n" +
//                "SELECT d,e,f FROM tableB as tb where d = {d:d} and e like '%{e:e}' or f in ({f})\n" +
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
//        String update = "update sys_user set a={a},b={b} where a={a}";
//        Map<String, Object> map = Maps.newHashMap();
////        map.put("column", new String[]{"a", "b"});
////        map.put("a", "'abc'");
////        map.put("b", "b");
////        map.put("c", new String[]{"c1", "c2"});
////        map.put("d", "d");
////        map.put("e", "e");
////        map.put("f", new String[]{"f1", "f2"});
////        map.put("g", "g");
////        map.put("h", new String[]{"h1", "h2"});
////        map.put("j", new String[]{"j1", "j2"});
////        map.put("ga", "ga'''");
////        map.put("gb", "gb");
//        map.put("order", "ad desc");
//
//        map.put("format", "%Y/%m/%d");
//
//        parserSQL(sql, map);
//    }
}
