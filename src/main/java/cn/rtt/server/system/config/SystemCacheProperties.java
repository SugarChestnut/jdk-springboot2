package cn.rtt.server.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author rtt
 * @date 2026/3/17 17:00
 */
@ConfigurationProperties(prefix = "system.cache")
@Component
@Data
public class SystemCacheProperties {
}
