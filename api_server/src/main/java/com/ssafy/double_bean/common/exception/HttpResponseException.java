package com.ssafy.double_bean.common.exception;

public class HttpResponseException extends RuntimeException {
    private final ErrorCode errorCode;

    public HttpResponseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
