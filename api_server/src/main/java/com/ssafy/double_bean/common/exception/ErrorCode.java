package com.ssafy.double_bean.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E0001", "Invalid request."),
    NOT_READABLE_REQUEST(HttpStatus.BAD_REQUEST, "E0002", "Not readable request."),
    BAD_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "E0003", "Given token's type is unexpected."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "E0004", "Invalid 'Authorization' header value."),
    ALREADY_PUBLISHED_STORY(HttpStatus.BAD_REQUEST, "E0005", "Can't modify already published story."),
    CANNOT_PLAY_WRITING_STORY(HttpStatus.BAD_REQUEST, "E0006", "Can't play story in 'WRITING' status."),
    SPOT_MODIFY_FAILED(HttpStatus.BAD_REQUEST, "E0007", "Given request to modify spot is invalid."),
    ONLY_ONE_WRITING_STORY(HttpStatus.BAD_REQUEST, "E0008", "Can't duplicate story with status 'WRITING'."),
    NOT_CLEARED_STORY(HttpStatus.BAD_REQUEST, "E0009", "User didn't cleared story yet."),
    NOT_PUBLISHED_STORY(HttpStatus.BAD_REQUEST, "E0010", "Requested story is not published."),
    ALREADY_CLEARED_PLAYING(HttpStatus.BAD_REQUEST, "E0011", "Already cleared playing."),
    INVALID_SPOT_CLEAR_ORDER(HttpStatus.BAD_REQUEST, "E0012", "You can't clear requested spot with that order."),
    ALREADY_CLEARED_SPOT(HttpStatus.BAD_REQUEST, "E0013", "Already cleared spot."),

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
    USER_ALREADY_PLAYING_GAME(HttpStatus.CONFLICT, "E0002", "Requested player is already playing game."),
    CANNOT_DELETE_STORY_WITH_PLAYER(HttpStatus.CONFLICT, "E0003", "Failed to do job because of requested story have playing records."),
    ALREADY_REVIEWED_STORY(HttpStatus.CONFLICT, "E0004", "Already reviewed story."),
    NOT_PLAYING_GAME(HttpStatus.CONFLICT, "E0005", "User is not playing game.");

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
