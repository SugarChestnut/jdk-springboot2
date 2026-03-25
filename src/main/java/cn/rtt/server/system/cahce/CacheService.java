package cn.rtt.server.system.cahce;

import java.time.Duration;

/**
 * @author rtt
 * @date 2026/1/14 13:50
 */
public interface CacheService {

    Object get(String key);

    void put(String key, Object value, Duration duration);

    void invalidate(String key);

    Object get(CacheMetaEnum c, Object k);

    void put(CacheMetaEnum c, Object k, Object value);

    void invalidate(CacheMetaEnum c, Object k);

    void invalidateAll();
}
