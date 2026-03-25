package cn.rtt.server.system.cahce;

import lombok.Getter;

import java.time.Duration;

/**
 * 缓存的key 常量
 */
@Getter
public enum CacheMetaEnum {

    USER("user:", Duration.ofDays(8)),
    USER_TOKEN_ACCESS("user_token_access:", Duration.ofMinutes(30)),
    USER_TOKEN_REFRESH("user_token_refresh:", Duration.ofDays(14)),
    REPEAT_SUBMIT_KEY("repeat_submit:", Duration.ofSeconds(5)),
    PWD_ERR_CNT_KEY("pwd_err_cnt:", Duration.ofMinutes(5)),
    CAPTCHA_CODE_KEY("captcha_code:", Duration.ofMinutes(5)),
    ;
    final String prefix;
    final Duration duration;

    CacheMetaEnum(String prefix, Duration duration) {
        this.prefix = prefix;
        this.duration = duration;
    }
}
