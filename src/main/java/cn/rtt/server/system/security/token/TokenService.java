package cn.rtt.server.system.security.token;

import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.utils.IpUtils;
import cn.rtt.server.system.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * token验证处理
 */
public interface TokenService {

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

    void invalidateUser(String userId);

    void invalidateAll();

    /**
     * 设置用户代理信息
     */
    default void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        loginUser.setIpaddr(IpUtils.getIpAddr());
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }
}
