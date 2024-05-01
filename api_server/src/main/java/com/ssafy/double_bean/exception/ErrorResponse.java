package com.ssafy.double_bean.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "발생 일시", example = "2024-05-01T07:56:22.134Z")
    private final LocalDateTime timestamp = LocalDateTime.now();
    @Schema(description = "에러 상세 코드", example = "E0001")
    private final String detailCode;
    @Schema(description = "상세 메시지", example = "Something went wrong.")
    private final String message;

    public ErrorResponse(String detailCode, String message) {
        this.detailCode = detailCode;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        ErrorResponse response = new ErrorResponse(errorCode.getDetailCode(), errorCode.getDetailMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public String getMessage() {
        return message;
    }
}
