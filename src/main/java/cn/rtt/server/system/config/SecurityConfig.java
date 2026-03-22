package cn.rtt.server.system.config;


import cn.rtt.server.system.security.filter.JwtAuthenticationFilter;
import cn.rtt.server.system.security.handler.AuthenticationEntryPointImpl;
import cn.rtt.server.system.security.handler.LogoutSuccessHandlerImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

/**
 * spring security配置
 *
 * @author ruoyi
 */
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@AllArgsConstructor
public class SecurityConfig {
    /**
     * 自定义用户认证逻辑
     */
    private final UserDetailsService userDetailsService;
    /**
     * 认证失败处理类
     */
    private final AuthenticationEntryPointImpl unauthorizedHandler;
    /**
     * token认证过滤器
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * 跨域过滤器
     */
    private final CorsFilter corsFilter;
    /**
     * 允许匿名访问的地址
     */
    private final PermitAllUrlProperties permitAllUrl;

    /**
     * 身份验证实现
     */
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)  // CSRF禁用
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用 HTTP basic
                .formLogin(AbstractHttpConfigurer::disable) // 禁用默认表单登录
                .logout(AbstractHttpConfigurer::disable)    // 禁用默认的登出
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configure(httpSecurity))
                .headers((headersCustomizer) -> headersCustomizer
                        // 默认会添加以下缓存控制头，强制浏览器不要缓存，关闭这个配置
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        // 允许同源嵌入
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                // 认证失败处理类
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 注解标记允许匿名访问的url, 实际是在添加 AuthorizationFilter，过滤请求
                .authorizeHttpRequests((requests) -> {
                    permitAllUrl.getUrls().forEach(url -> requests.antMatchers(url).permitAll());
                    requests.antMatchers("/**/login",
                                    "/pdf/pdf2Text",
                                    "/**/register",
                                    "/wxIntroduceService/**",
                                    "/**/captcha",
                                    "/**/api/**",
                                    "/usr/**",
                                    "/sysFile/file/**",
                                    "/**/sendLoginCode",
                                    "/captcha/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                // 添加JWT filter，在 SessionCreationPolicy.STATELESS 需要手动将 Authentication 置到上下文，同时用户路径
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 前后的分离需要添加 cors
                .addFilterBefore(corsFilter, JwtAuthenticationFilter.class)
                .build();
    }

    /**
     * 全局忽略，不会走 Filter
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/resources/**"));
    }
}
