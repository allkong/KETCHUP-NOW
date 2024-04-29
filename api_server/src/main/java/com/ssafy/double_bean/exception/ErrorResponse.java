package com.ssafy.double_bean.exception;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String detailCode;
    private final String message;

    public ErrorResponse(int status, String detailCode, String message) {
        this.status = status;
        this.detailCode = detailCode;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        ErrorResponse response = new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.getDetailCode(), errorCode.getDetailMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public String getMessage() {
        return message;
    }
}
