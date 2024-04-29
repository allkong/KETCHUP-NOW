package com.ssafy.double_bean.user.dto;

import com.ssafy.double_bean.user.model.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequestDto(
        @NotBlank(message = "loginId cannot be null or blank")
        @Size(min=1, max = 20, message = "Length of loginId must between [1, 20]")
        String loginId,
        @NotBlank(message = "password cannot be null or blank")
        @Size(min=3, max = 30, message = "Length of password must between [3, 30]")
        String password,
        @NotBlank(message = "nickname cannot be null or blank")
        @Size(min=3, max = 30, message = "Length of nickname must between [3, 30]")
        String nickname) {
        public UserEntity toRequestEntity() {
                UserEntity entity = new UserEntity();
                entity.setLoginId(loginId);
                entity.setNickname(nickname);
                entity.setPassword(password);
                return entity;
        }
}
