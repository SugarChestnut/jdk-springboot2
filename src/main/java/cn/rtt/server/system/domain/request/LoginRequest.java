package cn.rtt.server.system.domain.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户登录对象
 *
 */
@Slf4j
@Data
public class LoginRequest
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 验证码
     */
    private String code;

    /**
     * 滑动验证
     */
    private Double slideOffset;

}
