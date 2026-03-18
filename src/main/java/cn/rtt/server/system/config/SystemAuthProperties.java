package cn.rtt.server.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "system.auth")
@Data
public class SystemAuthProperties {

    private JwtConfig jwt = new JwtConfig();
    private LoginConfig login = new LoginConfig();

    // Token Configuration Inner Class
    @Data
    public static class JwtConfig {
        private String secret;
        private String privateKey;
        private String publicKey;
        private String header = "Authorization";
        private Duration accessTokenTtl = Duration.ofMillis(15);
        private Duration  refreshTokenTtl = Duration.ofDays(7);
    }

    // Login Configuration Inner Class
    @Data
    public static class LoginConfig {
        private int maxRetryCount = 5;
        private Duration lockTime = Duration.ofMinutes(5);
    }
}
