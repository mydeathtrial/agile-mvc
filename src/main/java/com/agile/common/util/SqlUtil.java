package com.agile.common.util;

import com.agile.common.base.Constant;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;

import java.util.*;

/**
 * 描述：
 * <p>创建时间：2018/12/6<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class SqlUtil{
    private static final String CurlyBracesLeft = "'\\W*\\{";
    private static final String CurlyBracesRight = "}\\W*'";
    private static final String CurlyBracesLeft2 = "'{";
    private static final String CurlyBracesRight2 = "}'";
    private static final String CurlyBracesLeft3 = "{";
    private static final String CurlyBracesRight3 = "}";
    private static final String ParamFormat = "{%s}";
    private static final String StringParamFormat = "'%s'";
    private static final String ReplaceNullCondition = " 1=1 ";
    private static final String ReplaceNull = "null";
    private static final String ReplaceCount = "count(1)";
    
    /**
     * 根据给定参数动态生成完成参数占位的查询条数sql语句
     * @param sql 原sql模板
     * @param parameters map格式的sql语句中的参数集合，使用{paramName}方式占位
     * @return 生成的sql结果
     */
    public static String parserCountSQL(String sql, Map<String,Object> parameters){
        sql = parserSQL( sql, parameters);
        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();
        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();
        List<SQLSelectItem> items = sqlSelectQueryBlock.getSelectList();
        items.removeAll(items);
        items.add(new SQLSelectItem(SQLUtils.toSQLExpr(ReplaceCount)));
        return sqlSelectQueryBlock.toString();
    }

    /**
     * 根据给定参数动态生成完成参数占位的sql语句
     * @param sql 原sql
     * @param parameters map格式的sql语句中的参数集合，使用{paramName}方式占位
     * @return 生成的sql结果
     */
    public static String parserSQL(String sql,Map<String,Object> parameters){
        if(!sql.contains("{"))return sql;
        sql = sql.replaceAll(CurlyBracesLeft,CurlyBracesLeft3).replaceAll(CurlyBracesRight,CurlyBracesRight3).replace(CurlyBracesLeft3,CurlyBracesLeft2).replace(CurlyBracesRight3,CurlyBracesRight2);

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        List<SQLSelectItem> select = sqlSelectQueryBlock.getSelectList();
        parsingSelectItem(select,parameters);

        SQLTableSource from = sqlSelectQueryBlock.getFrom();
        parsingTableSource(from,parameters);

        SQLExpr where = sqlSelectQueryBlock.getWhere();
        if(where != null){
            parserSQLObject(where,parameters);
        }


        SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
        if(groupBy != null){
            parserSQLObject(groupBy,parameters);
        }
        System.out.println(sqlSelectQueryBlock.toString());
        return sqlSelectQueryBlock.toString();
    }

    /**
     * 处理查询字段
     * @param select 查询字段集合
     * @param parameters 参数集合
     */
    private static void parsingSelectItem(List<SQLSelectItem> select,Map<String,Object> parameters){
        select.removeIf(sqlSelectItem -> !parser(Collections.singletonList(sqlSelectItem.getExpr()), parameters));
    }

    /**
     * 处理查询的from部分
     * @param from from部分
     * @param parameters 参数集合
     */
    private static void parsingTableSource(SQLTableSource from,Map<String,Object> parameters){
        if(from instanceof SQLSubqueryTableSource){
            String s = ((SQLSubqueryTableSource) from).getSelect().toString();
            String l = parserSQL(s.replace(CurlyBracesLeft2, CurlyBracesLeft3).replace(CurlyBracesRight2, CurlyBracesRight3), parameters);
            SQLStatement se = SQLParserUtils.createSQLStatementParser(l, JdbcUtils.MYSQL).parseStatement();
            ((SQLSubqueryTableSource) from).setSelect(((SQLSelectStatement) se).getSelect());
        }else if(from instanceof SQLJoinTableSource){
            SQLTableSource left = ((SQLJoinTableSource) from).getLeft();
            parsingTableSource(left,parameters);

            SQLTableSource right = ((SQLJoinTableSource) from).getRight();
            parsingTableSource(right,parameters);
        }
    }

    /**
     * sql分段，比如把where条件按照表达式拆分成段
     * @param sqlObject sql druid对象
     */
    private static List<SQLObject> getMuchPart(SQLObject sqlObject){
        List<SQLObject> result = new LinkedList<>();
        List<SQLObject> children = ((SQLExpr)sqlObject).getChildren();
        if(children!=null && children.size()>0){
            for (SQLObject child:children) {
                if(child instanceof SQLExpr){
                    List<SQLObject> grandson = ((SQLExpr) child).getChildren();
                    if(grandson==null || grandson.size()==0){
                        result.add(sqlObject);
                        break;
                    }else{
                        result.addAll(getMuchPart(child));
                    }
                }
            }
        }else{
            return getMuchPart(sqlObject.getParent());
        }
        return result;
    }

    /**
     * 处理sqlObject直接转转换占位符
     * @param sqlObject sql druid对象
     * @param parameters 参数集合
     */
    private static void parserSQLObject(SQLObject sqlObject,Map<String,Object> parameters){
        List<SQLObject> sqlPartInfo = null;
        if(sqlObject instanceof SQLExpr){
            sqlPartInfo = getMuchPart(sqlObject);
        }else if(sqlObject instanceof SQLSelectGroupByClause){
            SQLSelectGroupByClause proxy = ((SQLSelectGroupByClause) sqlObject);
            sqlPartInfo = getMuchPart(proxy.getHaving());
        }
        if(sqlPartInfo == null)return;
        for (SQLObject part:sqlPartInfo) {
            if(part instanceof SQLInListExpr){
                parsingInList((SQLInListExpr)part,parameters);
            }else if(part instanceof SQLBinaryOpExpr){
                parsingBinaryOp((SQLBinaryOpExpr)part,parameters);
            }
        }
    }

    /**
     * 直接处理sqlExpr中的占位参数，不符合的直接踢除
     * @param items sqlExpr集合
     * @param parameters 参数结集合
     * @return 返回是否处理成功
     */
    private static boolean parser(List<SQLExpr> items,Map<String,Object> parameters){
        for (SQLExpr item:items) {
            String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM,item.toString(),0);
            if(key!=null){
                Object value = parameters.get(key);
                if(parameters.get(key) == null || StringUtil.isEmpty(String.valueOf(value))){
                    return false;
                }else{
                    String format = item.toString().replace(String.format(ParamFormat, key), ParamFormat);
                    Object param = parameters.get(key);
                    SQLUtils.replaceInParent(item,SQLUtils.toSQLExpr(format.replace(ParamFormat,param.toString())));
                }
            }
        }
        return true;
    }

    /**
     * 处理where column in （）类型条件
     * @param c in的druid表达式
     * @param parameters 参数集合
     */
    private static void parsingInList(SQLInListExpr c,Map<String,Object> parameters){
        List<SQLExpr> items = c.getTargetList();
        if(items == null)return;
        List<SQLExpr> list = new ArrayList<>();
        for (SQLExpr item:items) {
            String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM,item.toString(),0);
            if(key!=null){
                Object value = parameters.get(key);
                if(parameters.get(key) != null && !StringUtil.isEmpty(String.valueOf(value))){
                    Object param = parameters.get(key);
                    if(param instanceof Iterable){
                        Iterator it = ((Iterable) param).iterator();
                        while (it.hasNext()){
                            list.add(SQLUtils.toSQLExpr(String.format(StringParamFormat,String.valueOf(it.next()))));
                        }
                    }else if(param.getClass().isArray()){
                        for (Object o:(Object[])param) {
                            list.add(SQLUtils.toSQLExpr(String.format(StringParamFormat,String.valueOf(o))));
                        }
                    }
                }
            }
        }
        if(list.size()>0){
            c.setTargetList(list);
        }else{
            if(!(c.getParent() instanceof SQLReplaceable)){
                c.setNot(!c.isNot());
                c.setTargetList(Collections.singletonList(SQLUtils.toSQLExpr(ReplaceNull)));
            }else{
                SQLUtils.replaceInParent(c,SQLUtils.toSQLExpr(ReplaceNullCondition));
            }

        }
    }

    /**
     * 处理普通where表达式
     * @param c where表达式段
     * @param parameters 参数集合
     */
    private static void parsingBinaryOp(SQLBinaryOpExpr c, Map<String,Object> parameters){
        boolean isParsing = parser(SQLBinaryOpExpr.split(c), parameters);
        if(!isParsing){
            if(!SQLUtils.replaceInParent(c,null)){
                SQLUtils.replaceInParent(c,SQLUtils.toSQLExpr(ReplaceNullCondition));
            }
        }
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("ids",new String[]{"1","123"});
        map.put("name","222");
        map.put("id","111");
        map.put("aaa","sys_users_id");
        map.put("bbb","name");
        map.put("ccc","tutors");
        SqlUtil.parserSQL("select * from sys_users where sys_users_id in ({ids})",map);
//        parserSQL("select * from sys_users where sys_users_id in ({ids}) and name = {name}",map);
//        parserSQL("\tSELECT {aaa},{bbb1}\n" +
//                "FROM sys_users\n" +
//                " GROUP BY sys_users_id,name HAVING sys_users_id in ({ids12}) ",map);
//        parserSQL("SELECT\n" +
//                "\tt.{bbb} as q \n" +
//                "FROM\n" +
//                "\tsys_users,( SELECT * FROM sys_users WHERE sys_users_id IN ( {ids} ) AND NAME = {name} ),sys_users1,( SELECT * FROM sys_users WHERE sys_users_id IN ( {ids} ) AND NAME = {name} ) t \n" +
//                "WHERE\n" +
//                "\tt.NAME = {name}",map);
    }
}
