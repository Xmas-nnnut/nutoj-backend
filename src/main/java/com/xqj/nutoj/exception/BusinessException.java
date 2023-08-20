package com.xqj.nutoj.exception;

import com.xqj.nutoj.common.ErrorCode;

/**
 * 自定义异常类
 * 给 RuntimeException 扩充了 code 字段，支持传递 errorCode
 * 自定义构造函数，更灵活快捷的设置字段。
 *
 * 通过定义这个自定义的异常类，可以在业务逻辑中抛出 BusinessException 异常，并携带相应的错误码和错误消息。
 * 这样可以更好地区分不同的业务异常，并在异常处理中进行相应的逻辑处理。
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    //构造方法，接受一个错误码和错误消息作为参数，并调用父类RuntimeException的构造方法来设置异常消息。
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    //构造方法，接受一个ErrorCode枚举类型作为参数，并根据该枚举类型的错误码和错误消息调用父类RuntimeException的构造方法来设置异常消息。
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
