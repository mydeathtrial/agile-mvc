package com.agile.common.filter;

import com.agile.common.factory.LoggerFactory;
import com.alibaba.druid.filter.FilterChain;
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
    protected void statementCreateAfter(StatementProxy statement) {
    }

    protected void statementPrepareAfter(PreparedStatementProxy statement) {
    }

    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
    }

    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
    }

    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
    }

    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        printLog(statement.getRawObject());
    }

    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
    }

    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        printLog(statement.getRawObject());
    }

    protected void statementExecuteBefore(StatementProxy statement, String sql) {
    }

    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        printLog(statement.getRawObject());
    }

    protected void statementExecuteBatchBefore(StatementProxy statement) {
    }

    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        printLog(statement.getRawObject());
    }

    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        printLog(statement.getRawObject(),error);
    }

    private void printLog(Statement statement){
        if(statement instanceof ClientPreparedStatement){
            try {
                LoggerFactory.DAO_LOG.info(((ClientPreparedStatement) statement).asSql());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void printLog(Statement statement,Throwable error){
        if(statement instanceof ClientPreparedStatement){
            try {
                LoggerFactory.DAO_LOG.error(((ClientPreparedStatement) statement).asSql(),error);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
