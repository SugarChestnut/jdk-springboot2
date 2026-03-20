package cn.rtt.server.system.security.token;

import lombok.Builder;
import lombok.Data;

/**
 * @author rtt
 * @date 2026/3/18 11:19
 */
@Builder
@Data
public class TokenPair {

    private String accessToken;       // Access Token字符串
    private String refreshToken;      // Refresh Token字符串
}
