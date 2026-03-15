package cn.rtt.server.system.constant;

import lombok.Getter;

@Getter
public enum StatusEnum {
    DELETED(-1, "删除"),
    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取对应的枚举
     *
     * @param code 状态码
     * @return 对应的枚举，如果未找到返回null
     */
    public static StatusEnum fromCode(int code) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断code是否有效
     *
     * @param code 状态码
     * @return 是否有效
     */
    public static boolean isValidCode(int code) {
        return fromCode(code) != null;
    }
}
