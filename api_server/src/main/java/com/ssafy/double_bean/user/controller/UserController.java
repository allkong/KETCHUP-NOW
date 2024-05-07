package com.ssafy.double_bean.user.controller;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.ErrorResponse;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import com.ssafy.double_bean.user.dto.SignUpRequestDto;
import com.ssafy.double_bean.user.dto.UserResponseDto;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {
    private final UserService userService;
    private final AuthenticatedUser requestedUser;

    public UserController(UserService userService, AuthenticatedUser requestedUser) {
        this.userService = userService;
        this.requestedUser = requestedUser;
    }

    @PostMapping("/sign-up")
    @Tag(name = "User API")
    @Operation(summary = "회원 가입 API", description = "요청된 정보로 새 회원을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "409", description = "중복이 허용되지 않은 필드에 이미 사용중인 값이 입력된 경우 (E0001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDto> doSignUp(@Valid @RequestBody final SignUpRequestDto dto) {
        userService.doSignUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    @Tag(name = "User API")
    @Operation(summary = "본인 정보 조회 API", description = "Header의 JWT로 사용자 정보를 식별해 요청한 사용자의 정보를 조회합니다. <br/>" +
            "JWT의 유효성과 만료 여부를 간단하게 확인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)))
    })
    public ResponseEntity<UserResponseDto> getRequestedUserInfo() {
        UserEntity userEntity = userService.findUserByUuid(requestedUser.getUuid()).orElseThrow(() -> new HttpResponseException(ErrorCode.UNKNOWN_USER));
        return ResponseEntity.ok(UserResponseDto.fromEntity(userEntity));
    }
}
