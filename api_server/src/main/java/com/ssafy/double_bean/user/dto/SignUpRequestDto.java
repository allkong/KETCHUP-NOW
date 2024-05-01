package com.ssafy.double_bean.user.dto;

import com.ssafy.double_bean.user.model.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원 가입 요청 DTO")
public record SignUpRequestDto(
        @Schema(description = "로그인 ID", example = "ssafy")
        @NotBlank(message = "loginId cannot be null or blank")
        @Size(min = 1, max = 20, message = "Length of loginId must between [1, 20]")
        String loginId,
        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "password cannot be null or blank")
        @Size(min = 3, max = 30, message = "Length of password must between [3, 30]")
        String password,
        @Schema(description = "닉네임", example = "김싸피")
        @NotBlank(message = "nickname cannot be null or blank")
        @Size(min = 3, max = 30, message = "Length of nickname must between [3, 30]")
        String nickname) {
    public UserEntity toRequestEntity() {
        UserEntity entity = new UserEntity();
        entity.setLoginId(loginId);
        entity.setNickname(nickname);
        entity.setPassword(password);
        return entity;
    }
}
