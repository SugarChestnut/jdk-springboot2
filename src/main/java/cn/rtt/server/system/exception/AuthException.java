package cn.rtt.server.system.exception;

import cn.rtt.server.system.constant.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = -4879677283847539655L;

    private ResultCode resultCode;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

}
