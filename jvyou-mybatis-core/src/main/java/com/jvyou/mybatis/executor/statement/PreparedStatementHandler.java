package com.jvyou.mybatis.executor.statement;

import com.jvyou.mybatis.executor.parameter.ParameterHandler;
import com.jvyou.mybatis.executor.resultset.ResultSetHandler;
import com.jvyou.mybatis.mapping.BoundSql;
import com.jvyou.mybatis.mapping.MappedStatement;
import com.jvyou.mybatis.session.Configuration;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @since 2024/5/15 22:37
 * ---description 预编译语句处理器
 */
public class PreparedStatementHandler implements StatementHandler {

    private final Configuration configuration;

    private final MappedStatement ms;

    private final Object parameter;

    private final BoundSql boundSql;

    private final ParameterHandler parameterHandler;

    private final ResultSetHandler resultSetHandler;


    public PreparedStatementHandler(Configuration configuration, MappedStatement ms, Object parameter) {
        this.configuration = configuration;
        this.ms = ms;
        this.parameter = parameter;
        this.boundSql = ms.getBoundSql(parameter);
        this.parameterHandler = configuration.newParameterHandler();
        this.resultSetHandler = configuration.newResultSetHandler();
    }

    @SneakyThrows
    @Override
    public Statement prepare(Connection connection) {
        return connection.prepareStatement(boundSql.getParsedSql());
    }

    @Override
    public void parameterize(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        parameterHandler.setParameters(ps, boundSql.getParamNames(), parameter);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public <T> T query(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return (T) resultSetHandler.handleResultSets(ms, ps);
    }

    @SneakyThrows
    @Override
    public int update(Statement statement) {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ps.getUpdateCount();
    }

    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    @Override
    public MappedStatement getMs() {
        return ms;
    }
}
