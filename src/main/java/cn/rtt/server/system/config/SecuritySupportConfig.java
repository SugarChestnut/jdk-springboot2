package cn.rtt.server.system.config;

import cn.rtt.server.system.security.verify.SlideVerifyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author rtt
 * @date 2026/2/12 13:12
 */
@Configuration
public class SecuritySupportConfig {

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SlideVerifyService slideVerifyService() {
        return new SlideVerifyService();
    }
}
