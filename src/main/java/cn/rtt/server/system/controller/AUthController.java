package cn.rtt.server.system.controller;

import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.LoginRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.security.AuthService;
import cn.rtt.server.system.security.token.TokenPair;
import cn.rtt.server.system.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 登录验证
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AUthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public Result<TokenPair> login(HttpServletRequest request, @RequestBody LoginRequest body) {
        return Result.success(authService.login(body));
    }

    @RequestMapping("/logout")
    public Result<?> logout() {
        return Result.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("getInfo")
    public Result<Map<String, Object>> getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Map<String, Object> data = new HashMap<>();
        data.put("user", loginUser.getUser());
        data.put("roles", loginUser.getUser().getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        data.put("permissions", loginUser.getPermissions());
        data.put("isDefaultModifyPwd", false);
        data.put("isPasswordExpired", false);
        return Result.success(data);
    }
}
