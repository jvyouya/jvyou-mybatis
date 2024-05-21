package com.jvyou.mybatis.executor;


import com.jvyou.mybatis.cache.Cache;
import com.jvyou.mybatis.mapping.MappedStatement;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/5/21 21:25
 * @Description 缓存执行器（装饰者模式）
 */
public class CachingExecutor implements Executor {

    private final Executor delegate;

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> query(MappedStatement ms, Object parameter) {
        // 如果缓存在，说明支持二级缓存
        Cache cache = ms.getCache();
        if (cache != null) {
            String key = ms.getCacheKey(parameter);
            Object cacheResult = cache.getObject(key);
            // 缓存中有数据直接返回
            if (cacheResult != null) {
                return (List<T>) cacheResult;
            }
            // 缓存中不存在则查询数据库或者一级缓存，查询结果存储在二级缓存里面
            List<Object> queryResult = delegate.query(ms, parameter);
            cache.putObject(key, queryResult);
            return (List<T>) queryResult;
        }
        // 缓存不存在，说明不支持二级缓存，走数据库查询或者一级缓存
        return delegate.query(ms, parameter);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) {
        // 更新前要清除二级缓存
        Cache cache = ms.getCache();
        if (cache != null) {
            cache.clear();
        }
        return delegate.update(ms, parameter);
    }

    @SneakyThrows
    @Override
    public void commit(boolean required) {
        delegate.commit(required);
    }

    @SneakyThrows
    @Override
    public void rollback(boolean required)  {
        delegate.rollback(required);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
