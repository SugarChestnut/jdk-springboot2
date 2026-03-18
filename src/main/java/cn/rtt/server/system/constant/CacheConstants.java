package cn.rtt.server.system.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public enum CacheConstants {

    LOGIN_TOKEN("login_tokens:", )

    private String prefix;
    private Long nanos;
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";


    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";


    public static final String USER_CHANGE_PHONE_KEY = "user_change_phone_key:";


}
