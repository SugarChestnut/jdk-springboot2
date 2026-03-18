package cn.rtt.server.system.security.service;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.config.SystemAuthProperties;
import cn.rtt.server.system.constant.CacheConstants;
import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.security.context.AuthenticationContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录密码方法
 *
 * @author ruoyi
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysPasswordService {

    private final CacheService cacheService;

    private final SystemAuthProperties systemConfigProperties;

    private final PasswordEncoder passwordEncoder;

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SysUser user) {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();

        Integer retryCount = (Integer) cacheService.get(username);

        if (retryCount == null) {
            retryCount = 0;
        }

        if (retryCount >= systemConfigProperties.getLogin().getMaxRetryCount()) {
            throw new SystemException(ResultCode.USER_PASSWORD_LOG_ERROR);
        }

        if (matches(user, password)) {
            clearLoginRecordCache(username);
        } else {
            retryCount = retryCount + 1;
            cacheService.expire(getCacheKey(username), retryCount, 300);
            throw new SystemException(ResultCode.LOGIN_ERROR);
        }
    }

    public boolean matches(SysUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        cacheService.invalid(getCacheKey(loginName));
    }
}
