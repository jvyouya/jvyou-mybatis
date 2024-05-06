package com.jvyou.mybatis.session;

import com.jvyou.mybatis.executor.SimpleSqlExecutor;
import com.jvyou.mybatis.executor.SqlExecutor;
import com.jvyou.mybatis.mapping.MappedStatement;
import com.jvyou.mybatis.plugin.InterceptorChain;
import com.jvyou.mybatis.plugin.LimitPlugin;
import com.jvyou.mybatis.plugin.SqlLogPlugin;
import com.jvyou.mybatis.type.IntegerParamHandler;
import com.jvyou.mybatis.type.ParamTypeHandler;
import com.jvyou.mybatis.type.StringParamHandler;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/4/27 20:03
 * @Description 核心配置类
 */
@Data
public class Configuration {

    // SQL 操作集合
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();
    // 类型处理器映射
    @SuppressWarnings("rawtypes")
    private final Map<Class, ParamTypeHandler> paramTypeHandlerMap = new HashMap<>();
    // 责任链
    private InterceptorChain interceptorChain = new InterceptorChain();

    public Configuration() {
        // 添加默认的类型处理器
        paramTypeHandlerMap.put(String.class, new StringParamHandler());
        paramTypeHandlerMap.put(Integer.class, new IntegerParamHandler());
        // 添加默认的插件拦截器
        interceptorChain.addInterceptor(new SqlLogPlugin());
        interceptorChain.addInterceptor(new LimitPlugin());
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
    public ParamTypeHandler getParamTypeHandler(Class type) {
        // 从参数类型处理器映射中获取与给定类型相匹配的处理器
        return paramTypeHandlerMap.get(type);
    }

    /**
     * 创建一个新的SqlExecutor对象，并使用责任链模式包装它。
     *
     * @return 返回包装后的 SqlExecutor 对象。
     */
    public SqlExecutor newSqlExecutor() {
        return interceptorChain.wrap(new SimpleSqlExecutor(this));
    }

}
