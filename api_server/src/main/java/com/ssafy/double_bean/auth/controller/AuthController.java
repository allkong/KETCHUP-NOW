package com.ssafy.double_bean.auth.controller;


import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.SingleTokenDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import com.ssafy.double_bean.auth.service.AuthService;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "회원에 대한 인증, 인가 및 권한 갱신 처리")
public class AuthController {
    private final String REFRESH_TOKEN_COOKIE_KEY = "refreshToken";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Tag(name = "Auth API")
    @Operation(summary = "로그인 API", description = "로그인 ID, 비밀번호를 받아 Access token과 Refresh token을 발급합니다. <br/>" +
            "Access token은 header에, Refresh token은 cookie에 전달되므로 유의해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT 발급 성공",
                    headers = {@Header(name = "Authorization", description = "Access JWT", schema = @Schema(example = "Bearer {{ JWT }}"))}),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<SingleTokenDto> doLogin(@Valid @RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenResponseDto generatedTokens = authService.getTokenWithLoginInfo(dto);

        HttpHeaders headers = new HttpHeaders();

        // Access token은 header에,
        headers.add("Authorization", "Bearer " + generatedTokens.accessToken());

        // Refresh token은 cookie에 써줌
        ResponseCookie refreshTokenCookie = getRefreshTokenResponseCookie(generatedTokens.refreshToken());

        return ResponseEntity.ok().headers(headers).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).build();
    }

    @PostMapping("/token")
    @Tag(name = "Auth API")
    @Operation(summary = "Access Token 재발급 API", description = "Refresh token을 받아 새 Access token을 발급합니다. (쿠키 기반)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT 발급 성공",
                    headers = {@Header(name = "authorization", description = "Access JWT", schema = @Schema(example = "Bearer {{ JWT }}"))}),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Void> issueToken(HttpServletRequest request, HttpServletResponse response) {
        String token = authService.resolveRefreshToken(request);
        if (token == null) {
            throw new HttpResponseException(ErrorCode.INVALID_TOKEN);
        }

        HttpHeaders headers = new HttpHeaders();
        SingleTokenDto result = authService.getTokenWithRefreshToken(token);
        headers.add("Authorization", "Bearer " + result.token());

        return ResponseEntity.ok().headers(headers).build();
    }

    private ResponseCookie getRefreshTokenResponseCookie(String refreshToken) {
        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_KEY, refreshToken)
                .domain("localhost") // TODO : 나중에 프론트 배포하면 바꿔줘야 함
                .path("/")
                .httpOnly(true)
                .maxAge(100000000)
                .sameSite("None")
                .build();
    }
}
