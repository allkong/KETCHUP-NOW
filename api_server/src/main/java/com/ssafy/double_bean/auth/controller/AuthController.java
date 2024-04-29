package com.ssafy.double_bean.auth.controller;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.SingleTokenResponseDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import com.ssafy.double_bean.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "회원에 대한 인증, 인가 및 권한 갱신 처리")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Tag(name = "Auth API")
    @Operation(summary = "로그인 API", description = "로그인 ID, 비밀번호를 받아 Access token과 Refresh token을 발급합니다. <br/>" +
            "일관성을 위해 Access token은 header에, Refresh token은 body에 전달되므로 유의해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT 발급 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleTokenResponseDto.class)),
                    headers = {@Header(name = "authorization", description = "Access JWT", schema = @Schema(example = "Bearer {{ JWT }}"))}),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    public ResponseEntity<SingleTokenResponseDto> doLogin(@Valid @RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenResponseDto generatedTokens = authService.getTokenWithLoginInfo(dto);
        response.setHeader("authorization", "Bearer " + generatedTokens.accessToken());
        return ResponseEntity.ok(new SingleTokenResponseDto(AuthService.TokenType.REFRESH, generatedTokens.refreshToken()));
    }
}
