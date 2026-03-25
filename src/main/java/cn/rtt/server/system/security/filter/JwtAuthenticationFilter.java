package cn.rtt.server.system.security.filter;


import cn.rtt.server.system.config.property.SystemAuthProperties;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.security.token.JwtValidateResult;
import cn.rtt.server.system.security.token.TokenService;
import cn.rtt.server.system.utils.SecurityUtils;
import cn.rtt.server.system.utils.ServletUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final SystemAuthProperties authProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        System.out.println(request.getRequestURI());
        LoginUser loginUser = null;
        String token = request.getHeader(authProperties.getJwt().getHeader());
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> optional = Arrays.stream(cookies)
                        .filter(cookie -> TokenService.REFRESH_TOKEN.equals(cookie.getName()))
                        .findFirst();
                if (optional.isPresent()) token = optional.get().getValue();
                if (token != null) {
                    JwtValidateResult jwtValidateResult = tokenService.validateToken(token);
                    if (!jwtValidateResult.isValid()) {
                        String cookie = String.format(
                                "%s=''; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=0",
                                TokenService.REFRESH_TOKEN);
                        ServletUtils.render401(response, cookie);
                        return;
                    }
                    loginUser = tokenService.getLoginUserWithRefreshToken(jwtValidateResult.getTokenId());
                }
            }
        } else {
            JwtValidateResult jwtValidateResult = tokenService.validateToken(token);
            if (!jwtValidateResult.isValid()) {
                ServletUtils.render401(response, null);
                return;
            }
            loginUser = tokenService.getLoginUserWithAccessToken(jwtValidateResult.getTokenId());
        }

        if (loginUser != null && SecurityUtils.getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }
}
