//package cn.rtt.server.system.config;
//
//import cn.rtt.server.system.config.property.CaffeineCacheProperties;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.caffeine.CaffeineCache;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author rtt
// * @date 2026/3/20 10:00
// */
//@Configuration
//@ConditionalOnProperty(name = "system.cache.type", havingValue = "caffeine")
//@ConditionalOnClass(name = {"com.github.benmanes.caffeine.cache.Caffeine", "org.springframework.cache.caffeine.CaffeineCacheManager"})
//public class DynamicCaffeineCacheConfig {
//
//    @Bean
//    public CacheManager cacheManager(CaffeineCacheProperties properties) {
//        return new CaffeineCacheManager() {
//            @Override
//            protected Cache createCaffeineCache(String name) {
//                CaffeineCacheProperties.CacheConfig config = properties.getDefaults();
//                if (properties.getCaches().containsKey(name)) {
//                    config = properties.getCaches().get(name);
//                }
//                return new CaffeineCache(name, Caffeine.newBuilder()
//                        .expireAfterWrite(config.getExpireAfterWrite().toNanos(), TimeUnit.NANOSECONDS)
//                        .maximumSize(config.getMaxSize())
//                        .recordStats()
//                        .build(), true);
//            }
//
//            @Override
//            public void setCacheNames(Collection<String> cacheNames) {
//                super.setCacheNames(Collections.emptySet());
//            }
//        };
//    }
//}
