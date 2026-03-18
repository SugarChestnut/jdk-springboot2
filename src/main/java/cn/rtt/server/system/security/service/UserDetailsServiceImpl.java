package cn.rtt.server.system.security.service;

import cn.rtt.server.system.constant.UserStatus;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUser(username);
        if (user == null || UserStatus.OK.getCode() != user.getStatus()) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUser(user);
        return loginUser;
    }
}
