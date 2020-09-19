package com.beier116.xuanzuo.common;

import lombok.Data;

@Data
public class RestResponse<T> {

    private Integer status;

    private String message;

    private String error;

    private Long timestamp;

    private String path;

    private T data;

    public RestResponse(Integer status, String message, String error, Long timestamp, String path, T data) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
        this.path = path;
        this.data = data;
    }

    public static <T> RestResponse<T> ok(String message, String error, Long timestamp, String path, T data) {
        return new RestResponse<>(
                ResponseStatus.SUCCESS,
                message,
                error,
                timestamp,
                path,
                data
        );
    }

    public static <T> RestResponse<T> failed(String message, String error, Long timestamp, String path, T data) {
        return new RestResponse<>(
                ResponseStatus.FAILED,
                message,
                error,
                timestamp,
                path,
                data
        );
    }

    public static <T> RestResponse<T> ok(String path, T data) {
        return ok("success", null, System.currentTimeMillis(), path, data);
    }

    public static <T> RestResponse<T> failed(String message, String error, String path) {
        return failed(message, error, System.currentTimeMillis(), path, null);
    }
}
