package com.ssafy.double_bean.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E0001", "Invalid request."),
    NOT_READABLE_REQUEST(HttpStatus.BAD_REQUEST, "E0002", "Not readable request."),

    // 401 Unauthorized
    UNKNOWN_USER(HttpStatus.UNAUTHORIZED,  "E0001","No such user with given credential."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "E0002", "Given token is expired."),

    // 409 Conflict
    DUPLICATED_FIELD_VALUE(HttpStatus.CONFLICT, "E0001", "There is duplicated field value. " +
            "Check duplication checking API result."),
    ;

    private final HttpStatus httpStatus;
    private final String detailCode;
    private final String detailMessage;

    ErrorCode(HttpStatus httpStatus, String detailCode, String detailMessage) {
        this.httpStatus = httpStatus;
        this.detailCode = detailCode;
        this.detailMessage = detailMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
