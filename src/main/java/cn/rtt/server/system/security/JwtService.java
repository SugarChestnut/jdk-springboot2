package cn.rtt.server.system.security;

import cn.rtt.server.system.config.SystemAuthProperties;
import cn.rtt.server.system.domain.LoginUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author rtt
 * @date 2026/3/18 10:19
 */
@Service
public class JwtService {
    private String final String TOKEN_TYPE = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${spring.application.name}")
    private String issuer;

    private final SystemAuthProperties authProperties;

    private final SecretKey key;

    public JwtService(SystemAuthProperties authProperties) {
        this.authProperties = authProperties;
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(authProperties.getJwt().getSecret()));
    }

    /**
     * 签发令牌
     */
    public TokenPair issueTokenPair(LoginUser user) {
        Instant now = Instant.now();
        String refreshTokenId = UUID.randomUUID().toString();

        // 签发Access Token
        String accessToken = Jwts.builder()
                .subject(user.getUser().getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(authProperties.getJwt().getAccessTokenTtl())))
                .claim(TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .signWith(key)
                .compact();

        // 签发Refresh Token
        String refreshToken = Jwts.builder()
                .subject(user.getUser().getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(authProperties.getJwt().getRefreshTokenTtl())))
                .id(refreshTokenId)
                .claim(TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .signWith(key)
                .compact();

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void storeToken() {

    }

    public boolean isTokenInvalid() {

        return false;
    }

    /**
     * 撤销单个token
     */
    public void revokeToken() {

    }

    /**
     * 撤销所有token
     */
    public void revokeAll() {

    }
}
