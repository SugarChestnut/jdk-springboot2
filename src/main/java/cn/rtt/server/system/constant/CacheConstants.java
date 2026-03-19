package cn.rtt.server.system.constant;

import lombok.Getter;

import java.time.Duration;

/**
 * 缓存的key 常量
 */
@Getter
public enum CacheConstants {

    USER_TOKEN_ACCESS("user_token_access:", Duration.ofMinutes(15)),
    USER_TOKEN_REFRESH("user_token_refresh:", Duration.ofDays(7)),
    REPEAT_SUBMIT_KEY("repeat_submit:", Duration.ofSeconds(5)),
    PWD_ERR_CNT_KEY("pwd_err_cnt:", Duration.ofMinutes(5)),
    CAPTCHA_CODE_KEY("captcha_code:", Duration.ofMinutes(5)),
    ;
    final String prefix;
    final Duration duration;

    CacheConstants(String prefix, Duration duration) {
        this.prefix = prefix;
        this.duration = duration;
    }
}
