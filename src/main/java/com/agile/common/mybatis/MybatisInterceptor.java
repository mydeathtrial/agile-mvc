package com.agile.common.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 描述：
 * <p>创建时间：2018/12/17<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Component
@Intercepts(@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class}))
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = getActuralHandlerObject(invocation);

        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);

        String sql = statementHandler.getBoundSql().getSql();

        BoundSql boundSql = statementHandler.getBoundSql();
        Object paramObject = boundSql.getParameterObject();

        PageRequest pageRequest = PageExecutor.getPageRequest(paramObject);

        if (pageRequest == null || !checkIsSelectFalg(sql)) {
            return invocation.proceed();
        }
        //修改sql
        return updateSql2Limit(invocation, metaStatementHandler, boundSql, pageRequest.getPageNumber(), pageRequest.getPageSize());
    }

    //从代理对象中分离出真实statementHandler对象,非代理对象
    private StatementHandler getActuralHandlerObject(Invocation invocation) {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        Object object = null;
        //分离代理对象链，目标可能被多个拦截器拦截，分离出最原始的目标类
        while (metaStatementHandler.hasGetter("h")) {
            object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }

        if (object == null) {
            return statementHandler;
        }
        return (StatementHandler) object;
    }

    //判断是否是select语句，只有select语句，才会用到分页
    private boolean checkIsSelectFalg(String sql) {
        String trimSql = sql.trim();
        int index = trimSql.toLowerCase().indexOf("select");
        return index == 0;
    }

    //修改原始sql语句为分页sql语句
    private Object updateSql2Limit(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql, int page, int pageSize) throws InvocationTargetException, IllegalAccessException, SQLException {
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        //构建新的分页sql语句
        String limitSql = "select * from (" + sql + ") $_paging_table limit ?,?";
        //修改当前要执行的sql语句
        metaStatementHandler.setValue("delegate.boundSql.sql", limitSql);
        //相当于调用prepare方法，预编译sql并且加入参数，但是少了分页的两个参数，它返回一个PreparedStatement对象
        PreparedStatement ps = (PreparedStatement) invocation.proceed();
        //获取sql总的参数总数
        int count = ps.getParameterMetaData().getParameterCount();
        //设置与分页相关的两个参数
        ps.setInt(count - 1, page * pageSize);
        ps.setInt(count, pageSize);
        return ps;
    }

    @Override
    public Object plugin(Object o) {
        if (Executor.class.isAssignableFrom(o.getClass())) {
            PageExecutor executor = new PageExecutor((Executor) o);
            return Plugin.wrap(executor, this);
        } else if (o instanceof StatementHandler) {
            return Plugin.wrap(o, this);
        }
        return o;
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
