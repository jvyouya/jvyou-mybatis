package com.jvyou.mybatis.session.defaults;

import com.jvyou.mybatis.binding.MapperProxyFactory;
import com.jvyou.mybatis.exception.TooManyResultsException;
import com.jvyou.mybatis.executor.SqlExecutor;
import com.jvyou.mybatis.mapping.MappedStatement;
import com.jvyou.mybatis.session.Configuration;
import com.jvyou.mybatis.session.SqlSession;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/4/28 20:08
 * @Description 默认 SqlSession
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final SqlExecutor sqlExecutor;

    public DefaultSqlSession(Configuration configuration, SqlExecutor sqlExecutor) {
        this.configuration = configuration;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public int insert(String statementId, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        return sqlExecutor.update(mappedStatement, parameter);
    }

    @Override
    public int update(String statementId, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        return sqlExecutor.update(mappedStatement, parameter);
    }

    @Override
    public int delete(String statementId, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        return sqlExecutor.update(mappedStatement, parameter);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        List<T> list = selectList(statementId, parameter);
        if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statementId, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statementId);
        return sqlExecutor.query(mappedStatement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statementId) {
        return selectList(statementId, null);
    }

    @Override
    public <T> T getMapper(Class<T> mapperClass) {
        return new MapperProxyFactory().getProxy(mapperClass, this);
    }

    @Override
    public void close() {
        sqlExecutor.close();
    }

    @SneakyThrows
    @Override
    public void commit() {
        sqlExecutor.commit(false);
    }

    @SneakyThrows
    @Override
    public void rollback() {
        sqlExecutor.rollback(false);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
