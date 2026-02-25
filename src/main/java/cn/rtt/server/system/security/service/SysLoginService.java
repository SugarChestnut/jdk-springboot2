package cn.rtt.server.system.security.service;


import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.constant.CacheConstants;
import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.security.TokenService;
import cn.rtt.server.system.security.context.AuthenticationContextHolder;
import cn.rtt.server.system.service.SysMenuService;
import cn.rtt.server.system.service.SysUserService;
import cn.rtt.server.system.utils.IpUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 登录
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysLoginService {

    private final TokenService tokenService;

    private final SysUserService sysUserService;

    private final AuthenticationManager authenticationManager;

    private final CacheService cacheService;

    private final SysMenuService menuService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        loginUser.setPermissions(menuService.getPermission(loginUser.getUserId()));
        loginUser.setSuperAdmin(RoleEnum.isSuperAdmin(loginUser.getUser().getRoles()));
        loginUser.setAdmin(RoleEnum.isAdmin(loginUser.getUser().getRoles()));
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 唯一标识
     */
    private void validateCaptcha(String code, String uuid) {

        Object value = cacheService.get(CacheConstants.CAPTCHA_CODE_KEY + uuid);
        if (value == null || !Strings.CS.equals((String) value, code)) {
            throw new SystemException(ResultCode.CODE_ERROR);
        }
    }

    /**
     * 登录前置校验
     */
    private void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new SystemException(ResultCode.LOGIN_ERROR);
        }

        // IP黑名单校验
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUserService.updateUser(sysUser);
    }
}
