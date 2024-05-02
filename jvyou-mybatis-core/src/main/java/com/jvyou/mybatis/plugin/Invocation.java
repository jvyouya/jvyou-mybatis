package com.jvyou.mybatis.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/5/2 21:00
 * @Description 方法调用器，包含方法调用的目标对象、要调用的方法以及调用方法时传递的参数。
 */
@Data
@AllArgsConstructor
public class Invocation {
    /**
     * 目标对象
     */
    private final Object target;
    /**
     * 要调用的方法
     */
    private final Method method;
    /**
     * 调用方法时传递的参数
     */
    private final Object[] args;

    /**
     * 调用方法
     *
     * @return 方法调用结果
     * @throws Exception 方法调用异常
     */
    @SneakyThrows
    public Object proceed()  {
        return method.invoke(target, args);
    }
}
