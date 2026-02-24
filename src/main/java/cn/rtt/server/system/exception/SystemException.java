package cn.rtt.server.system.exception;

import cn.rtt.server.system.constant.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = -4879677283847539655L;

    private int errorCode;

    private String errorMessage;

    private String exceptionMessage;

    private Exception exception;

    public SystemException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public SystemException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.errorCode = resultCode.getCode();
        this.errorMessage = resultCode.getMsg();
    }

    public SystemException(int errorCode, String errorMessage, Exception exception) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    public SystemException(String errorMessage, String exceptionMessage) {
        super(errorMessage);
        this.exceptionMessage = exceptionMessage;
        this.errorMessage = errorMessage;
    }

    public SystemException(int errorCode, String errorMessage, String exceptionMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exceptionMessage = exceptionMessage;
    }
}
