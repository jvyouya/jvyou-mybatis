package com.jvyou.mybatis.session;

import com.jvyou.mybatis.cache.Cache;
import com.jvyou.mybatis.cache.PerpetualCache;
import com.jvyou.mybatis.executor.CachingExecutor;
import com.jvyou.mybatis.executor.Executor;
import com.jvyou.mybatis.executor.SimpleExecutor;
import com.jvyou.mybatis.executor.parameter.DefaultParameterHandler;
import com.jvyou.mybatis.executor.parameter.ParameterHandler;
import com.jvyou.mybatis.executor.resultset.DefaultResultSetHandler;
import com.jvyou.mybatis.executor.resultset.ResultSetHandler;
import com.jvyou.mybatis.executor.statement.PreparedStatementHandler;
import com.jvyou.mybatis.executor.statement.StatementHandler;
import com.jvyou.mybatis.mapping.MappedStatement;
import com.jvyou.mybatis.plugin.InterceptorChain;
import com.jvyou.mybatis.plugin.LimitPlugin;
import com.jvyou.mybatis.plugin.SqlLogPlugin;
import com.jvyou.mybatis.transaction.Transaction;
import com.jvyou.mybatis.type.*;
import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @since 2024/4/27 20:03
 * ---description 核心配置类
 */
@Data
public class Configuration {

    // SQL 操作集合
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();
    // 类型处理器映射
    @SuppressWarnings("rawtypes")
    private final Map<Class, TypeHandler> paramTypeHandlerMap = new HashMap<>();
    // 责任链
    private InterceptorChain interceptorChain = new InterceptorChain();
    // 全局开启二级缓存
    protected boolean cacheEnabled = true;
    // 缓存 Map
    protected final Map<String, Cache> caches = new HashMap<>();
    //数据源
    private DataSource dataSource;

    public Configuration() {
        // 添加默认的类型处理器
        paramTypeHandlerMap.put(String.class, new StringHandler());
        paramTypeHandlerMap.put(Integer.class, new IntegerHandler());
        paramTypeHandlerMap.put(Long.class, new LongHandler());
        paramTypeHandlerMap.put(Double.class, new DoubleHandler());

        // 添加默认的插件拦截器
        interceptorChain.addInterceptor(new SqlLogPlugin());
        interceptorChain.addInterceptor(new LimitPlugin());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 添加一个映射语句到映射语句集合中。
     *
     * @param mappedStatement 映射语句对象，包含了SQL语句及其相关配置信息。
     */
    public void addMappedStatement(MappedStatement mappedStatement) {
        // 将映射语句对象添加到映射语句集合中，使用ID作为键
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    /**
     * 根据指定的ID从映射语句集合中获取对应的映射语句对象。
     *
     * @param id 映射语句的唯一标识符。
     * @return 返回与指定ID匹配的映射语句对象，如果没有找到则返回null。
     */
    public MappedStatement getMappedStatement(String id) {
        // 从映射语句集合中获取指定ID的映射语句
        return mappedStatements.get(id);
    }

    /**
     * 根据给定的类型获取对应的参数类型处理器。
     *
     * @param type 需要获取参数类型处理器的类型Class对象。
     * @return 返回与给定类型相匹配的参数类型处理器，如果找不到匹配的处理器则返回null。
     */
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getParamTypeHandler(Class type) {
        // 从参数类型处理器映射中获取与给定类型相匹配的处理器
        return paramTypeHandlerMap.get(type);
    }

    /**
     * 创建一个新的SqlExecutor对象，并使用责任链模式包装它。
     *
     * @return 返回包装后的 SqlExecutor 对象。
     */
    public Executor newSqlExecutor(Transaction transaction) {
        SimpleExecutor executor = new SimpleExecutor(this, transaction);
        // 统一通过 CachingExecutor 对执行器进行装饰，在解析 Configuration对象中存在了是否开启二级缓存
        // 并包装了 MappedStatement 的缓存对象，CachingExecutor 里面会通过对 MS 的缓存对象是否存在进行判断是否走二级缓存
        return interceptorChain.wrap(new CachingExecutor(executor));
    }

    public ResultSetHandler newResultSetHandler() {
        return interceptorChain.wrap(new DefaultResultSetHandler(this));
    }

    public ParameterHandler newParameterHandler() {
        return interceptorChain.wrap(new DefaultParameterHandler(this));
    }

    /**
     * 新建陈述语句处理器都对象，语句处理器被拦截器责任链进行代理包装
     *
     * @param ms        MappedStatement
     * @param parameter 参数对象
     * @return 陈述语句处理器
     */
    public StatementHandler newStatementHandler(MappedStatement ms, Object parameter) {
        return interceptorChain.wrap(new PreparedStatementHandler(this, ms, parameter));
    }

    /**
     * 获取缓存
     *
     * @param id 缓存ID
     * @return 缓存
     */
    public Cache getCache(String id) {
        return caches.computeIfAbsent(id, k -> new PerpetualCache(id));
    }
}
