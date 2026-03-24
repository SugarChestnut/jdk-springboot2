package cn.rtt.server.system.security;

import cn.rtt.server.system.constant.Permission;
import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.LoginRequest;
import cn.rtt.server.system.exception.AuthException;
import cn.rtt.server.system.security.token.TokenPair;
import cn.rtt.server.system.security.token.TokenService;
import cn.rtt.server.system.service.SysMenuService;
import cn.rtt.server.system.service.SysUserService;
import cn.rtt.server.system.utils.IpUtils;
import cn.rtt.server.system.utils.SecurityUtils;
import cn.rtt.server.system.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author rtt
 * @date 2026/3/18 13:39
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final SysUserService userService;
    private final SysMenuService menuService;

    /**
     * 登录验证
     */
    public TokenPair login(LoginRequest body) {
        Authentication a = SecurityUtils.getAuthentication();
        if (a instanceof UsernamePasswordAuthenticationToken) {
            LoginUser u = SecurityUtils.getLoginUser();
            tokenService.invalidateUser(u.getUserId());
        }
        // 登录前置校验
        loginPreCheck(body);
        // 用户验证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Set<String> permission = menuService.getPermission(loginUser.getUserId());
        if (loginUser.getSuperAdmin()) permission.add(Permission.SUPER_ADMIN);
        loginUser.setPermissions(permission);
        loginUser.setSuperAdmin(RoleEnum.isSuperAdmin(loginUser.getUser().getRoles()));
        loginUser.setAdmin(RoleEnum.isAdmin(loginUser.getUser().getRoles()));
        loginUser.setLoginTime(Instant.now());
        setUserAgent(loginUser);
        userService.updateLoginIp(loginUser.getUserId(), IpUtils.getIpAddr());
        return tokenService.issueTokenPair(loginUser);
    }

    public void logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        tokenService.invalidateAccessToken(loginUser.getAccessTokenId());
        tokenService.invalidateRefreshToken(loginUser.getAccessTokenId());
        tokenService.invalidateUser(loginUser.getUserId());
    }

    public String refresh() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ReentrantLock lock = loginUser.getLock();
        lock.lock();
        try {
            Instant expireTime = loginUser.getExpireTime();
            if (expireTime.isBefore(Instant.now().plusSeconds(10))) {
                return tokenService.refreshToken(loginUser);
            } else {
                return loginUser.getRefreshTokenId();
            }
        } finally {
            lock.unlock();
        }
    }

    private void loginPreCheck(LoginRequest body) {
        if (StringUtils.isAnyBlank(body.getUsername(), body.getPassword())) {
            throw new AuthException(ResultCode.LOGIN_ERROR);
        }
        // 其他验证，比如 登录次数过多
    }

    /**
     * 设置用户代理信息
     */
    private void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        loginUser.setIpaddr(IpUtils.getIpAddr());
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }
}
