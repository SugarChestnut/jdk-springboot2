package cn.rtt.server.system.exception;

import cn.rtt.server.system.constant.ResultCode;

/**
 * @author rtt
 * @date 2025/12/31 10:03
 */
public class OperationException extends  RuntimeException{

    private static final long serialVersionUID = -4879677283847539655L;

    private final int errorCode;

    private final String errorMessage;

    public OperationException(ResultCode status) {
        super(status.getMsg());
        this.errorCode = status.getCode();
        this.errorMessage = status.getMsg();
    }

    public OperationException(ResultCode status, String msg) {
        super(msg);
        this.errorCode = status.getCode();
        this.errorMessage = msg;
    }
}
