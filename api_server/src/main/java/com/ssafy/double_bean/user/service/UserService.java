package com.ssafy.double_bean.user.service;

import com.ssafy.double_bean.user.dto.SignUpRequestDto;
import com.ssafy.double_bean.user.dto.UserResponseDto;

public interface UserService {
	// 회원 가입을 수행하고, 생성된 사용자 객체를 반환한다.
	UserResponseDto doSignUp(SignUpRequestDto dto);
}
