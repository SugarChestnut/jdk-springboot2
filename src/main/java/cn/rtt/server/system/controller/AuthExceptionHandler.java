package cn.rtt.server.system.controller;

import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.exception.AuthException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rtt
 * @date 2026/3/18 14:22
 */
@ControllerAdvice
@ResponseBody
public class AuthExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> accessDeniedException(AccessDeniedException e) {
        return Result.error(ResultCode.UN_AUTH);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<?> usernameNotFoundException(UsernameNotFoundException e) {
        return Result.error(ResultCode.USER_NULL);
    }

    @ExceptionHandler(AuthException.class)
    public Result<?> authException(AuthException e) {
        return Result.error(e.getResultCode());
    }
}
