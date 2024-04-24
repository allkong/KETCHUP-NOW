package com.ssafy.double_bean.user.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.entity.UserEntityBuilder;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("사용자 및 비밀번호 생성, 연결 성공")
	void insertUserAndPassword_success() {
		// Given
		String loginId = "loginId";
		String hashedPassword = "asajkcz";
		String salt = "asjkzkbasdkzxclkansd";
		String nickname = "SSAFY";

		// When
		UserEntity dto = UserEntityBuilder.getForInsert(loginId, hashedPassword, salt, nickname);
		boolean isInserted = userRepository.insert(dto) == 1;

		// Then
		// 생성 성공
		assertThat(isInserted).isTrue();

		// loginId로 검색 가능
		Optional<UserEntity> searchResult = userRepository.findByLoginId(dto.getLoginId());
		assertThat(searchResult).isPresent();

		// Password 및 salt 연결 성공
		UserEntity insertedUser = searchResult.get();
		assertThat(insertedUser.getPassword()).isNotNull();
		assertThat(insertedUser.getSalt()).isNotNull();
	}

	@Test
	@DisplayName("loginId로 사용자 검색")
	void findByLoginId_existMember_success() {
		// Given
		final String TARGET_LOGIN_ID = "loginId";
		UserEntity insertDto = UserEntityBuilder.getForInsert(TARGET_LOGIN_ID, "hashedpassword", "salt", "nickname");
		userRepository.insert(insertDto);

		// When
		Optional<UserEntity> loginIdSearchResult = userRepository.findByLoginId(TARGET_LOGIN_ID);

		// Then
		assertThat(loginIdSearchResult).isPresent();
		UserEntity searchedUser = loginIdSearchResult.get();
		assertThat(searchedUser.getLoginId()).isEqualTo(TARGET_LOGIN_ID);
	}

	@Test
	@DisplayName("id로 사용자 검색")
	void findById_existMember_success() {
		// Given
		final String TARGET_LOGIN_ID = "loginId";
		UserEntity insertDto = UserEntityBuilder.getForInsert(TARGET_LOGIN_ID, "hashedpassword", "salt", "nickname");
		userRepository.insert(insertDto);
		UserEntity insertedUser = userRepository.findByLoginId(TARGET_LOGIN_ID).get();

		// When
		Optional<UserEntity> idSearchResult = userRepository.findById(insertedUser.getId());

		// Then
		assertThat(idSearchResult).isPresent();
		UserEntity searchedUser = idSearchResult.get();
		assertThat(searchedUser.getLoginId()).isEqualTo(TARGET_LOGIN_ID);
	}

	@Test
	@DisplayName("uuid로 사용자 검색")
	void findByUuid_existMember_success() {
		// Given
		final String TARGET_LOGIN_ID = "loginId";
		UserEntity insertDto = UserEntityBuilder.getForInsert(TARGET_LOGIN_ID, "hashedpassword", "salt", "nickname");
		userRepository.insert(insertDto);
		UserEntity searchedUser = userRepository.findByLoginId(TARGET_LOGIN_ID).get();

		// When
		Optional<UserEntity> uuidSearchResult = userRepository.findByUuid(searchedUser.getUuid());

		// Then
		assertThat(uuidSearchResult).isPresent();
	}

	@Test
	@DisplayName("존재하지 않는 사용자에 대한 검색")
	void findByLoginId_notExistMember_returnsOptionalNull() {
		// Given
		String unknownLoginId = "asdkjxcbvlka132";

		// When
		Optional<UserEntity> searchResult = userRepository.findByLoginId(unknownLoginId);

		// Then
		assertThat(searchResult).isNotPresent();
	}
}
