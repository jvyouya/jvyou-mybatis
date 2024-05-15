package com.jvyou.mybatis.builder;

import cn.hutool.core.util.ClassUtil;
import com.jvyou.mybatis.annotations.Delete;
import com.jvyou.mybatis.annotations.Insert;
import com.jvyou.mybatis.annotations.Select;
import com.jvyou.mybatis.annotations.Update;
import com.jvyou.mybatis.mapping.MappedStatement;
import com.jvyou.mybatis.mapping.SqlCommandType;
import com.jvyou.mybatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/4/27 19:59
 * @Description XML配置构建器
 */
public class XMLConfigBuilder {

    public Configuration parse() {
        Configuration configuration = new Configuration();
        // 解析 Mapper
        parseMapper(configuration);
        return configuration;
    }

    private void parseMapper(Configuration configuration) {

        Set<Class<?>> classes = ClassUtil.scanPackage("com.jvyou.mybatis.mapper");
        for (Class<?> aClass : classes) {
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                String originalSql = "";
                SqlCommandType sqlCommandType = SqlCommandType.SELECT;
                if (method.isAnnotationPresent(Select.class)) {
                    originalSql = method.getAnnotation(Select.class).value();
                } else if (method.isAnnotationPresent(Update.class)) {
                    originalSql = method.getAnnotation(Update.class).value();
                    sqlCommandType = SqlCommandType.UPDATE;
                } else if (method.isAnnotationPresent(Insert.class)) {
                    originalSql = method.getAnnotation(Insert.class).value();
                    sqlCommandType = SqlCommandType.INSERT;
                } else if (method.isAnnotationPresent(Delete.class)) {
                    originalSql = method.getAnnotation(Delete.class).value();
                    sqlCommandType = SqlCommandType.DELETE;
                }
                // 是否返回多行
                boolean isSelectMany = false;
                // 获取 Mapper 方法的返回值类型
                Class<?> returnType = null;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof Class) {
                    returnType = (Class<?>) genericReturnType;
                } else if (genericReturnType instanceof ParameterizedType) {
                    isSelectMany = true;
                    returnType = ((ParameterizedType) genericReturnType).getActualTypeArguments().length > 0
                            ? (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0]
                            : (Class<?>) ((ParameterizedType) genericReturnType).getRawType();
                }
                // 构建 MappedStatement
                MappedStatement mappedStatement = MappedStatement.builder()
                        .id(aClass.getName() + "." + method.getName())
                        .sql(originalSql)
                        .resultType(returnType)
                        .isSelectMany(isSelectMany)
                        .sqlCommandType(sqlCommandType)
                        .build();
                configuration.addMappedStatement(mappedStatement);
            }
        }

    }

}
