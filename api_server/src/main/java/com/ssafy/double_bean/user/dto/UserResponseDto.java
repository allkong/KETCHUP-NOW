package com.ssafy.double_bean.user.dto;

import com.ssafy.double_bean.user.model.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "사용자 정보에 대한 응답 DTO")
public record UserResponseDto(
        @Schema(description = "고유 식별자", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID uuid,
        @Schema(description = "로그인 ID", example = "ssafy")
        String loginId,
        @Schema(description = "닉네임", example = "김싸피")
        String nickname,
        @Schema(description = "생성 일시", example = "2024-05-01T07:56:22.134Z")
        LocalDateTime createdAt,
        @Schema(description = "최종 수정 일시", example = "2024-05-01T07:56:22.134Z")
        LocalDateTime modifiedAt) {
    public static UserResponseDto fromEntity(UserEntity entity) {
        return new UserResponseDto(entity.getUuid(), entity.getLoginId(), entity.getNickname(), entity.getCreatedAt(), entity.getModifiedAt());
    }
}
