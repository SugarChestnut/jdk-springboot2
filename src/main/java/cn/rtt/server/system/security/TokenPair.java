package cn.rtt.server.system.security;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * @author rtt
 * @date 2026/3/18 11:19
 */
@Builder
@Data
public class TokenPair {

    private String accessToken;       // Access Token字符串
    private Instant accessTokenExpiresAt; // Access Token过期时间
    private String refreshToken;      // Refresh Token字符串
    private Instant refreshTokenExpiresAt;// Refresh Token过期时间
    private String refreshTokenId;      // Refr
}
