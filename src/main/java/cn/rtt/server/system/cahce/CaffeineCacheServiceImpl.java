package cn.rtt.server.system.cahce;

import cn.rtt.server.system.constant.CacheMetaEnum;
import com.github.benmanes.caffeine.cache.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
/**
 * @author rtt
 * @date 2026/1/14 13:51
 */
@Service
public class CaffeineCacheServiceImpl extends AbstractCacheService {

    private final Cache<String, CacheWrapper> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, CacheWrapper>() {

                @Override
                public long expireAfterCreate(@NonNull String key, @NonNull CacheWrapper value, long currentTime) {
                    return value.getDuration().toNanos();
                }

                @Override
                public long expireAfterUpdate(@NonNull String key, @NonNull CacheWrapper value, long currentTime, long currentDuration) {
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(@NonNull String key, @NonNull CacheWrapper value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Override
    public Object get(String key) {
        CacheWrapper v = cache.getIfPresent(key);
        return v == null ? null : v.getData();
    }

    public Object get(CacheMetaEnum c, Object k) {
        return get(c.getPrefix() + k);
    }


    @Override
    public void put(String key, Object value, Duration duration) {
        cache.put(key, new CacheWrapper(duration, value));
    }

    public void put(CacheMetaEnum c, Object k, Object value) {
        put(c.getPrefix() + k, value, c.getDuration());
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }

    public void invalidate(CacheMetaEnum c, Object k) {
        invalidate(c.getPrefix() + k);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

}
