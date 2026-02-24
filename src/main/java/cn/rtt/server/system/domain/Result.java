package cn.rtt.server.system.domain;

import cn.rtt.server.system.constant.ResultCode;
import lombok.Data;

/**
 * @author rtt
 * @date 2026/2/5 16:03
 */
@Data
public class Result<T> {
    /**
     * 响应代码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    private Boolean flag;

    /**
     * 响应结果
     */
    private T data;

    public Result() {
    }
    public Result(ResultCode errorInfo) {
        this.code = errorInfo.getCode();
        this.msg = errorInfo.getMsg();
    }

    /**
     * 成功
     */
    public static Result<?> success() {
        return success(null);
    }

    /**
     * 成功
     */
    public static <T> Result<T> success(T data) {
        Result<T> rb = new Result<>();
        rb.setCode(ResultCode.SUCCESS.getCode());
        rb.setMsg(ResultCode.SUCCESS.getMsg());
        rb.setFlag(true);
        rb.setData(data);
        return rb;
    }

    /**
     * 失败
     */
    public static Result<?> error(ResultCode errorInfo) {
        Result<?> rb = new Result<>();
        rb.setCode(errorInfo.getCode());
        rb.setMsg(errorInfo.getMsg());
        rb.setData(null);
        rb.setFlag(false);
        return rb;
    }

    /**
     * 失败
     */
    public static Result<String> error(int code, String message) {
        Result<String> rb = new Result<>();
        rb.setCode(code);
        rb.setMsg(message);
        rb.setData(null);
        rb.setFlag(false);
        return rb;
    }


    /**
     * 失败
     */
    public static Result<String> error(String message) {
        Result<String> rb = new Result<>();
        rb.setCode(-1);
        rb.setMsg(message);
        rb.setFlag(false);
        rb.setData(null);
        return rb;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error(T data) {
        Result<T> rb = new Result<>();
        rb.setCode(-1);
        rb.setMsg("异常");
        rb.setFlag(false);
        rb.setData(data);
        return rb;
    }

    public static Result<?> judge(boolean status) {
        if (status) {
            return success();
        } else {
            return error();
        }
    }

    public static Result<?> numCompare(int status) {
        if (status > 0) {
            return success();
        } else {
            return error();
        }
    }

    public static Result<?> error() {
        return error(ResultCode.ERROR);
    }
}