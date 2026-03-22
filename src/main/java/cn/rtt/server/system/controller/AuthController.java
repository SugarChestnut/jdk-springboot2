package cn.rtt.server.system.controller;

import cn.rtt.server.system.config.property.SystemAuthProperties;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.request.LoginRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.security.AuthService;
import cn.rtt.server.system.security.token.TokenPair;
import cn.rtt.server.system.security.token.TokenService;
import cn.rtt.server.system.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 登录验证
 */
@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final SystemAuthProperties authProperties;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        TokenPair tokenPair = authService.login(body);
        ResponseCookie cookie = ResponseCookie.from(TokenService.REFRESH_TOKEN, tokenPair.getRefreshToken())
                .maxAge(Math.toIntExact(authProperties.getJwt().getRefreshTokenTtl().toSeconds()))
                .path("/refresh")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body("Login Success");
    }

    @RequestMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        ResponseCookie cookie = ResponseCookie.from(TokenService.REFRESH_TOKEN, "")
                .maxAge(0)           // 立即过期
                .path("/refresh")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body("Login Success");
    }

    @RequestMapping("/refresh")
    public ResponseEntity<String> refresh() {
        return ResponseEntity.ok(authService.refresh());
    }

    /**
     * 获取用户信息
     */
    @GetMapping("getInfo")
    public Result<Map<String, Object>> getInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie);
            }
        }
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
