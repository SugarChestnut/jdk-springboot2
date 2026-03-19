package cn.rtt.server.system.cahce;

import java.time.Duration;

/**
 * @author rtt
 * @date 2026/1/14 13:50
 */
public interface CacheService {

    Object get(String key);

    void expire(String key, Object value, Duration duration);

    void invalid(String key);
}
