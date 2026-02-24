package cn.rtt.server.system.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * EhCacheCacheManager：使用 EhCache 作为缓存技术
 * GuavaCacheManager：使用 Google 的 Guava Cache 作为缓存技术
 * RedisCacheManager：使用 Redis 作为缓存技术
 * CaffeineCacheManager：使用 Caffeine 作为缓存技术
 *
 * @author rtt
 * @date 2025/11/27 08:44
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.MINUTES));
        return cacheManager;
    }
}
