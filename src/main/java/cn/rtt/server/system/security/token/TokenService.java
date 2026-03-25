package cn.rtt.server.system.security.token;

import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.utils.IpUtils;
import cn.rtt.server.system.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * token验证处理
 */
public interface TokenService {

    String ACCESS_TOKEN = "Access_token";

    String REFRESH_TOKEN = "Refresh_token";

    LoginUser getLoginUserWithAccessToken(String tokenId);

    LoginUser getLoginUserWithRefreshToken(String tokenId);

    String refreshToken(LoginUser user);

    /**
     * 签发令牌
     */
    TokenPair issueTokenPair(LoginUser user);

    JwtValidateResult validateToken(String token);

    /**
     * 撤销单个token
     */
    void invalidateAccessToken(String tokenId);

    void invalidateRefreshToken(String tokenId);

    void invalidateUser(Long userId);

    void invalidateAll();
}
