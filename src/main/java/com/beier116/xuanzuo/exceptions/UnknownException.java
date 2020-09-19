package com.beier116.xuanzuo.exceptions;

public class UnknownException extends Exception {
    public UnknownException() {
        super("未知异常，请联系开发者");
    }
}
