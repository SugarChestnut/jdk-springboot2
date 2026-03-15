package cn.rtt.server.system.constant;

import lombok.Getter;

/**
 * 用户状态
 *
 * @author ruoyi
 */
@Getter
public enum UserStatus {
    OK(0, "正常"),
    DISABLE(1, "停用"),
    DELETED(2, "删除")
    ;

    private final int code;
    private final String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(int code) {
        for (UserStatus status : values()) {
            if (status.code == code) return status.desc;
        }
        return null;
    }
}
