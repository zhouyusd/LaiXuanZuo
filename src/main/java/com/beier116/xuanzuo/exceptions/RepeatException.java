package com.beier116.xuanzuo.exceptions;

public class RepeatException extends Exception {
    public RepeatException() {
        super("已经预选成功，请勿重新选座");
    }
}
