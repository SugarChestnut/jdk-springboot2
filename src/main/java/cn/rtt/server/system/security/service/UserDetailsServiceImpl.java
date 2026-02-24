package cn.rtt.server.system.security.service;


import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.UserStatus;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysPasswordService passwordService;

    private final SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUser(username);
        if (user == null || UserStatus.OK.getCode() != user.getStatus()) {
            throw new SystemException(ResultCode.LOGIN_ERROR);
        }
        passwordService.validate(user);
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUser(user);
        return loginUser;
    }
}
