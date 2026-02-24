package cn.rtt.server.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "system.config")
@Data
public class SystemConfigProperties {

    private TokenConfig token = new TokenConfig();
    private LoginConfig login = new LoginConfig();

    // Token Configuration Inner Class
    @Data
    public static class TokenConfig {
        private String secret = "abcdefghijklmnopqrstuvwxyz";
        private String header = "Authorization";
        private int expireTime = 60;
    }

    // Login Configuration Inner Class
    @Data
    public static class LoginConfig {
        private int maxRetryCount = 5;
        private int lockTime = 300;
    }
}
