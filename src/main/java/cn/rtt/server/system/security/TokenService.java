package cn.rtt.server.system.security;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.config.SystemAuthProperties;
import cn.rtt.server.system.constant.CacheConstants;
import cn.rtt.server.system.constant.Constants;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.utils.IpUtils;
import cn.rtt.server.system.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Service
@Slf4j
@AllArgsConstructor
public class TokenService {

    private static final Integer TOKEN_REFRESH_INTERVAL = 20;

    private final CacheService cacheService;

    private final SystemAuthProperties systemConfig;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            Claims claims = parseToken(token);
            // 解析对应的权限以及用户信息
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            Object value = cacheService.get(userKey);
            if (value != null) {
                return (LoginUser) value;
            }
        }
        return null;
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            cacheService.invalid(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，如果距离上一次刷新缓存超过20分钟，更新token缓存
     */
    public boolean verifyToken(LoginUser loginUser) {
        if (loginUser.getExpireTime().isBefore(LocalDateTime.now())) return false;
        if (!loginUser.getExpireTime().minusMinutes(TOKEN_REFRESH_INTERVAL).isBefore(LocalDateTime.now()))
            refreshToken(loginUser);
        return true;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setExpireTime(LocalDateTime.now().plusMinutes(systemConfig.getToken().getExpireTime()));
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        cacheService.put(userKey, loginUser);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        loginUser.setIpaddr(IpUtils.getIpAddr());
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(systemConfig.getToken().getSecret()));
        return Jwts.builder().claims(claims).signWith(key).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(systemConfig.getToken().getSecret()));
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    /**
     * 获取请求token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(systemConfig.getToken().getHeader());
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }
}
