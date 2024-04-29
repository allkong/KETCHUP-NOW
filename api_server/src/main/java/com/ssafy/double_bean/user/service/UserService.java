package com.ssafy.double_bean.user.service;

import com.ssafy.double_bean.user.dto.SignUpRequestDto;
import com.ssafy.double_bean.user.model.entity.UserEntity;

import java.util.Optional;

public interface UserService {
	// 회원 가입을 수행하고, 생성된 사용자 객체를 반환한다.
	void doSignUp(SignUpRequestDto dto);

	// 로그인 ID로 사용자를 찾는다.
	Optional<UserEntity> findUserByLoginId(String loginId);
}
