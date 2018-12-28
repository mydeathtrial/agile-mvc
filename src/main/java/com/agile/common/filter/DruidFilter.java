package com.agile.common.filter;

import com.agile.common.factory.LoggerFactory;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.mysql.cj.jdbc.ClientPreparedStatement;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * 描述：
 * <p>创建时间：2018/12/14<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class DruidFilter extends FilterEventAdapter {
    @Override
    protected void statementCreateAfter(StatementProxy statement) {
    }

    @Override
    protected void statementPrepareAfter(PreparedStatementProxy statement) {
    }

    @Override
    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        printLog(statement.getRawObject());
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        printLog(statement.getRawObject());
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        printLog(statement.getRawObject());
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        printLog(statement.getRawObject());
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        printLog(statement.getRawObject(), error);
    }

    /**
     * 打印日志
     *
     * @param statement Statement
     */
    private void printLog(Statement statement) {
        if (statement instanceof ClientPreparedStatement) {
            try {
                LoggerFactory.getDaoLog().info(((ClientPreparedStatement) statement).asSql().replaceAll("\\s", " "));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印异常日志
     *
     * @param statement Statement
     */
    private void printLog(Statement statement, Throwable error) {
        if (statement instanceof ClientPreparedStatement) {
            try {
                LoggerFactory.getDaoLog().error(((ClientPreparedStatement) statement).asSql().replaceAll("\\s", " "), error);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
