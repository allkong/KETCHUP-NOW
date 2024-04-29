package com.ssafy.double_bean.auth.controller;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.SingleTokenResponseDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import com.ssafy.double_bean.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<SingleTokenResponseDto> doLogin(@Valid @RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenResponseDto generatedTokens = authService.getTokenWithLoginInfo(dto);
        response.setHeader("authorization", "Bearer " + generatedTokens.accessToken());
        return ResponseEntity.ok(new SingleTokenResponseDto(AuthService.TokenType.REFRESH, generatedTokens.refreshToken()));
    }
}
