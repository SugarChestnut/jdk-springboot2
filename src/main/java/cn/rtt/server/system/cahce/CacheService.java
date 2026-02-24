package cn.rtt.server.system.cahce;

/**
 * @author rtt
 * @date 2026/1/14 13:50
 */
public interface CacheService {

    Object get(String key);

    void put(String key, Object value);

    void expire(String key, Object value, int seconds);

    void invalid(String key);
}
