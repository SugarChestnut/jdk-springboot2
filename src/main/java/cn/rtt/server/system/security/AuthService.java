package cn.rtt.server.system.security;

import cn.rtt.server.system.constant.Permission;
import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.LoginRequest;
import cn.rtt.server.system.exception.AuthException;
import cn.rtt.server.system.security.context.AuthenticationContextHolder;
import cn.rtt.server.system.service.SysMenuService;
import cn.rtt.server.system.service.SysUserService;
import cn.rtt.server.system.utils.IpUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author rtt
 * @date 2026/3/18 13:39
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SysUserService userService;
    private final SysMenuService menuService;

    /**
     * 登录验证
     */
    public TokenPair login(LoginRequest body) {
        // 登录前置校验
        loginPreCheck(body);
        // 用户验证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            AuthenticationContextHolder.setContext(authenticationToken);
            authentication = authenticationManager.authenticate(authenticationToken);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Set<String> permission = menuService.getPermission(loginUser.getUserId());
        if (loginUser.getSuperAdmin()) permission.add(Permission.SUPER_ADMIN);
        loginUser.setPermissions(permission);
        loginUser.setSuperAdmin(RoleEnum.isSuperAdmin(loginUser.getUser().getRoles()));
        loginUser.setAdmin(RoleEnum.isAdmin(loginUser.getUser().getRoles()));
        userService.updateLoginIp(loginUser.getUserId(), IpUtils.getIpAddr());
        // 生成token
        return jwtService.issueTokenPair(loginUser);
    }


    private void loginPreCheck(LoginRequest body) {
        if (StringUtils.isAnyBlank(body.getUsername(), body.getPassword())) {
            throw new AuthException(ResultCode.LOGIN_ERROR);
        }
        // 其他验证，比如 登录次数过多
    }

    public void logout() {

    }
}
