package com.jvyou.mybatis.executor;

import com.jvyou.mybatis.mapping.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @since 2024/4/28 17:40
 * ---description SQL 执行器
 */
public interface Executor {

    /**
     * 执行查询
     *
     * @param ms        MappedStatement 映射语句对象
     * @param parameter 参数（传递过来的可能是一个 Map 集合）
     * @param <T>       实体类集合
     * @return 实体类集合
     */
    <T> List<T> query(MappedStatement ms, Object parameter);

    /**
     * 执行更新
     *
     * @param ms        MappedStatement 映射语句对象
     * @param parameter 参数（传递过来的可能是一个 Map 集合）
     * @return 更新的行数
     */
    int update(MappedStatement ms, Object parameter);

    /**
     * 提交事务
     *
     * @param required 是否强制提交提交
     */
    void commit(boolean required) ;

    /**
     * 回滚事务
     *
     * @param required 是否强制回滚
     */
    void rollback(boolean required) ;

    void close();
}
