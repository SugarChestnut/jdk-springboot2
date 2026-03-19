package cn.rtt.server.system.cahce;

import com.github.benmanes.caffeine.cache.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @date 2026/1/14 13:51
 */
@Service
public class CaffeineCacheServiceImpl extends AbstractCacheService {

    private final Cache<String, CacheWrapper> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, CacheWrapper>() {

                @Override
                public long expireAfterCreate(String key, CacheWrapper value, long currentTime) {
                    return value.getDuration().toNanos();
                }

                @Override
                public long expireAfterUpdate(String key, CacheWrapper value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(String key, CacheWrapper value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Override
    public Object get(String key) {
        Duration duration = getDuration(key);
        cache.

        if (redirectCache.containsKey(key)) {
            return redirectCache.get(key).getIfPresent(key);
        }
        return null;
    }


    @Override
    public void expire(String key, Object value, Duration duration) {
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
