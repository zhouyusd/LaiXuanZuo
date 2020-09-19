package com.beier116.xuanzuo.exceptions;

import com.beier116.xuanzuo.common.RestResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public RestResponse jsonErrorHandler(HttpServletRequest req, Exception e) {
        return RestResponse.failed(
                e.getMessage(),
                e.toString(),
                System.currentTimeMillis(),
                req.getRequestURI(),
                null
        );
    }
}
