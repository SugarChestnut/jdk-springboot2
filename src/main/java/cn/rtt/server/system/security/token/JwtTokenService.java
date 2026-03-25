package cn.rtt.server.system.security.token;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.config.property.SystemAuthProperties;
import cn.rtt.server.system.cahce.CacheMetaEnum;
import cn.rtt.server.system.domain.LoginUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * TODO 设备指纹，比如 refresh_token 只能用于获取 access_token
 * @author rtt
 * @date 2026/3/18 10:19
 */
@Service
public class JwtTokenService implements TokenService {

    @Value("${spring.application.name}")
    private String issuer;

    private final SystemAuthProperties authProperties;

    private final CacheService cacheService;

    private final SecretKey key;

    public JwtTokenService(SystemAuthProperties authProperties,
                           CacheService cacheService) {
        this.authProperties = authProperties;
        this.cacheService = cacheService;
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(authProperties.getJwt().getSecret()));
    }

    @Override
    public LoginUser getLoginUserWithAccessToken(String tokenId) {
        Object v = cacheService.get(CacheMetaEnum.USER_TOKEN_ACCESS, tokenId);
        return v == null ? null : (LoginUser) v;
    }

    @Override
    public LoginUser getLoginUserWithRefreshToken(String tokenId) {
        Object v = cacheService.get(CacheMetaEnum.USER_TOKEN_REFRESH, tokenId);
        return v == null ? null : (LoginUser) v;
    }

    @Override
    public String refreshToken(LoginUser user) {
        return issueAccessToken(user);
    }

    private String issueAccessToken(LoginUser user) {
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(user.getUser().getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(authProperties.getJwt().getAccessTokenTtl())))
                .id(tokenId)
                .signWith(key)
                .compact();
        user.setAccessTokenId(tokenId);
        user.setAccessToken(token);
        user.setExpireTime(now.plus(authProperties.getJwt().getAccessTokenTtl()));
        cacheService.put(CacheMetaEnum.USER_TOKEN_ACCESS, tokenId, user);
        return token;
    }

    private String issueRefreshToken(LoginUser user) {
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(user.getUser().getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(authProperties.getJwt().getRefreshTokenTtl())))
                .id(tokenId)
                .signWith(key)
                .compact();
        user.setRefreshTokenId(tokenId);
        cacheService.put(CacheMetaEnum.USER_TOKEN_REFRESH, tokenId, user);
        return token;
    }

    /**
     * 签发令牌
     */
    @Override
    public TokenPair issueTokenPair(LoginUser user) {
        String accessToken = issueAccessToken(user);
        String refreshToken = issueRefreshToken(user);
        cacheService.put(CacheMetaEnum.USER, user.getUserId(), user);
        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public JwtValidateResult validateToken(String token) {
        try {
            String tokenId = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getId();
            return new JwtValidateResult(true, false, tokenId);
        } catch (ExpiredJwtException e) {
            return new JwtValidateResult(false, true, null);
        } catch (JwtException e) {
            return new JwtValidateResult(false, false, null);
        }
    }

    /**
     * 撤销单个token
     */
    @Override
    public void invalidateAccessToken(String tokenId) {
        cacheService.invalidate(CacheMetaEnum.USER_TOKEN_ACCESS, tokenId);
    }

    @Override
    public void invalidateRefreshToken(String tokenId) {
        cacheService.invalidate(CacheMetaEnum.USER_TOKEN_REFRESH, tokenId);
    }

    @Override
    public void invalidateUser(Long userId) {
        Object o = cacheService.get(CacheMetaEnum.USER, userId);
        if (o == null) return;
        LoginUser user = (LoginUser) o;
        cacheService.invalidate(CacheMetaEnum.USER_TOKEN_ACCESS, user.getAccessTokenId());
        cacheService.invalidate(CacheMetaEnum.USER_TOKEN_REFRESH, user.getRefreshTokenId());
        cacheService.invalidate(CacheMetaEnum.USER, userId);
    }

    /**
     * 撤销所有token
     */
    @Override
    public void invalidateAll() {
        cacheService.invalidateAll();
    }
}
