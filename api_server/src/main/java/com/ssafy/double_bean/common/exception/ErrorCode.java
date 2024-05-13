package com.ssafy.double_bean.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E0001", "Invalid request."),
    NOT_READABLE_REQUEST(HttpStatus.BAD_REQUEST, "E0002", "Not readable request."),
    BAD_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "E0003", "Given token's type is unexpected."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "E0004", "Invalid 'Authorization' header value."),
    ALREADY_PUBLISHED_STORY(HttpStatus.BAD_REQUEST, "E0005", "Can't modify already published story."),

    // 401 Unauthorized
    UNKNOWN_USER(HttpStatus.UNAUTHORIZED, "E0001", "No such user with given credential."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E0002", "Failed to parse given token."),

    // 403 Forbidden
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "E0001", "Given token is expired."),
    HAS_NO_OWNERSHIP(HttpStatus.FORBIDDEN, "E0002", "You have no permission to access this object."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "E0001", "Cannot find requested resource"),

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
