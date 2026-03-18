package cn.rtt.server.system.constant;

import lombok.Getter;

@Getter
public enum ResultCode {
    // 通用成功失败
    SUCCESS(200, "成功!"),
    ERROR(-1, "失败!"),

    // 用户端错误
    BODY_NOT_MATCH(400, "请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH(401, "请求的数字签名不匹配!"),
    NOT_FOUND(404, "数据不存在!"),
    ALREADY_FOUND(405, "数据已存在!"),
    PARAM_ERROR(406, "请求参数错误!"),

    // 服务端错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误!"),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!"),

    // 登录、权限、认证相关
    LOGIN_ERROR(101, "用户名或者密码错误!"),
    USER_STOP(102, "用户已停用!"),
    USER_NULL(103, "用户不存在!"),
    CODE_ERROR(104, "验证码错误!"),
    PHONE_USED(107, "手机号已被绑定!"),
    USER_ALREADY(108, "用户已存在!"),
    UN_LOGIN(110, "未登录，请进行登录!"),
    UN_AUTH(111, "未授权访问!"),
    TOKEN_INVALID_OR_EXPIRED(112, "认证信息无效或已过期!"),
    USER_PASSWORD_LOG_ERROR(117, "登录失败次数过多！请5分钟后重试!"),
    ;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误描述
     */
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
