package cn.rtt.server.system.controller;

import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public Result<?> systemException(SystemException e) {
        if (e.getException() != null) log.error(e.getExceptionMessage(), e.getException());
        int code = e.getErrorCode() != 0 ? e.getErrorCode() : ResultCode.ERROR.getCode();
        return Result.error(code, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> accessDeniedException(AccessDeniedException e) {
        return Result.error(ResultCode.UN_AUTH);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> missingServletRequestParameterException(Exception e) {
        return Result.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> illegalArgumentException(Exception e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> exception(Exception e) throws Exception {
        log.error("系统错误", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
