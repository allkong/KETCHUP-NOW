package com.ssafy.double_bean.story.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;

@ActiveProfiles("dev")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoryRepositoryTest {
	@Autowired
	private StoryRepository storyRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	@DisplayName("첫 스토리 생성 성공")
	void createFirstStory_success() {
		// Given
		String loginId = "unknownIda9248isb";
		String nickname = "unknownNicknamea9248isb";
		userRepository.insert(new UserEntity(-1, null, loginId, "password", "salt", nickname, null, null));
		UserEntity user = userRepository.getAll().stream().filter(u -> u.getLoginId().equals(loginId)).findFirst().get();
		
		// When
		StoryEntity requestEntity = new StoryEntity(42, StoryStatus.PUBLISHED, "title", "description", "인천", "서구", null, null);
		storyRepository.createFirstStory(user.getId(), requestEntity);
		int insertedStoryId = requestEntity.getId();
	
		// Then
		// version, status는 항상 고정된 값으로 들어감
		Optional<StoryEntity> findResult = storyRepository.findById(insertedStoryId);
		System.out.println(storyRepository.getAll());
		
		System.out.println(findResult);
	}
}
