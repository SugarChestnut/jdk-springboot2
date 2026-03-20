//package cn.rtt.server.system.config.property;
//
//import lombok.Data;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author rtt
// * @date 2026/3/20 09:08
// */
//@Component
//@ConfigurationProperties(prefix = "system.cache.caffeine")
//@Data
//@ConditionalOnProperty(name = "system.cache.type", havingValue = "caffeine")
//@ConditionalOnClass(name = {"com.github.benmanes.caffeine.cache.Caffeine", "org.springframework.cache.caffeine.CaffeineCacheManager"})
//public class CaffeineCacheProperties {
//
//    private Map<String, CacheConfig> caches = new HashMap<>();
//    private CacheConfig defaults = new CacheConfig();
//
//    @Data
//    public static class CacheConfig {
//        private Duration expireAfterWrite = Duration.ofMinutes(10);
//        private Long maxSize = 1000L;
//    }
//}
