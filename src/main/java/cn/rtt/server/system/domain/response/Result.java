package cn.rtt.server.system.domain.response;

import cn.rtt.server.system.constant.ResultCode;
import lombok.Data;

@Data
public class Result<T> {

    private boolean flag;

    private int code;

    private String msg;

    private T data;

    public Result() {
    }

    public Result(ResultCode status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
        flag = this.code == ResultCode.SUCCESS.getCode();
    }

    /**
     * 成功
     */
    public static Result<?> success() {
        Result<?> rb = new Result<>();
        rb.setCode(ResultCode.SUCCESS.getCode());
        rb.setMsg(ResultCode.SUCCESS.getMsg());
        rb.setFlag(true);
        return rb;
    }

    public static <T> Result<T> success(T data) {
        Result<T> rb = new Result<>();
        rb.setCode(ResultCode.SUCCESS.getCode());
        rb.setMsg(ResultCode.SUCCESS.getMsg());
        rb.setData(data);
        rb.setFlag(true);
        return rb;
    }

    /**
     * 失败
     */
    public static Result<?> error(ResultCode errorInfo) {
        Result<?> rb = new Result<>();
        rb.setCode(errorInfo.getCode());
        rb.setMsg(errorInfo.getMsg());
        rb.setFlag(false);
        return rb;
    }

    /**
     * 失败
     */
    public static Result<?> error(int code, String message) {
        Result<?> rb = new Result<>();
        rb.setCode(code);
        rb.setMsg(message);
        rb.setFlag(false);
        return rb;
    }

    /**
     * 失败
     */
    public static Result<?> error(String message) {
        Result<?> rb = new Result<>();
        rb.setCode(ResultCode.ERROR.getCode());
        rb.setMsg(message);
        rb.setFlag(false);
        return rb;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error(T data) {
        Result<T> rb = new Result<>();
        rb.setCode(-1);
        rb.setMsg("异常");
        rb.setData(data);
        rb.setFlag(false);
        return rb;
    }
}
