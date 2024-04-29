package com.ssafy.double_bean.user.controller;

import com.ssafy.double_bean.user.dto.SignUpRequestDto;
import com.ssafy.double_bean.user.dto.UserResponseDto;
import com.ssafy.double_bean.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> doSignUp(@Valid @RequestBody final SignUpRequestDto dto) {
        userService.doSignUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
