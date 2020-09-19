package com.beier116.xuanzuo.exceptions;

public class SessionExpiredException extends Exception {
    public SessionExpiredException() {
        super("微信SessionID已过期");
    }
}
