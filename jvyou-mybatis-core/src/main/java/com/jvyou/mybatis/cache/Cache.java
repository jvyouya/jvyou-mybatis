package com.jvyou.mybatis.cache;

/**
 * @author 橘柚
 * @version 1.0-SNAPSHOT
 * @Date 2024/5/21 13:45
 * @Description 缓存接口
 */
public interface Cache {

    String getId();

    void putObject(String key, Object value);

    Object getObject(String key);

    Object removeObject(String key);

    void clear();

}
