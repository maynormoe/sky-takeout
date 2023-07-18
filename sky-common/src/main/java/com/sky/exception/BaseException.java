package com.sky.exception;

/**
 * 业务异常
 * @author Maynormoe
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
