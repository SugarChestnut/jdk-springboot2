package cn.rtt.server.system.cahce;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @date 2026/1/14 13:51
 */
@Service
public class CaffeineCacheServiceImpl implements CacheService {

    private final Cache<String, Object> cache5Minutes = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .initialCapacity(10)
            .maximumSize(1000)
            .removalListener(new RemovalListenerImpl())
            .build();

    private final Cache<String, Object> cache1Hour = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .initialCapacity(10)
            .maximumSize(10000)
            .removalListener(new RemovalListenerImpl())
            .build();

    private final Cache<String, Object> cacheNoExpire = Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(200)
            .build();

    public final ConcurrentHashMap<String, Cache<String, Object>> redirectCache = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        if (redirectCache.containsKey(key)) {
            return redirectCache.get(key).getIfPresent(key);
        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        redirectCache.put(key, cacheNoExpire);
        cacheNoExpire.put(key, value);
    }

    @Override
    public void expire(String key, Object value, int seconds) {
        if (seconds <= 300) {
            redirectCache.put(key, cache5Minutes);
            cache5Minutes.put(key, value);
        } else {
            redirectCache.put(key, cache1Hour);
            cache1Hour.put(key, value);
        }
    }

    @Override
    public void invalid(String key) {
        if (redirectCache.containsKey(key)) {
            redirectCache.get(key).invalidate(key);
            redirectCache.remove(key);
        }
    }

    private class RemovalListenerImpl implements RemovalListener<String, Object> {

        @Override
        public void onRemoval(@Nullable String key, @Nullable Object value, @NonNull RemovalCause cause) {
            if (key != null) redirectCache.remove(key);
        }
    }
}
