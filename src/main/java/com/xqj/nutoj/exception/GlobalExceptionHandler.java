package com.xqj.nutoj.exception;

import com.xqj.nutoj.common.BaseResponse;
import com.xqj.nutoj.common.ErrorCode;
import com.xqj.nutoj.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 利用 Spring AOP，在调用方法前后进行额外的处理
 *
 * 捕获代码中所有的异常，内部消化，让前端得到更详细的业务报错 / 信息
 * 同时屏蔽掉项目框架本身的异常（不暴露服务器内部状态）
 * 集中处理，比如记录日志
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 用于处理BusinessException类型的异常。
     * 当捕获到BusinessException异常时，会记录异常信息到日志中，
     * 并通过ResultUtils.error()方法创建一个包含错误码和错误消息的BaseResponse对象返回。
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
