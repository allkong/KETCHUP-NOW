package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StoryRepositoryTest {
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
        StoryEntity requestEntity = new StoryEntity(null, 42, StoryStatus.PUBLISHED, "title", "description", "인천", "서구", null, null);
        storyRepository.createFirstStory(user.getId(), requestEntity);
        int insertedStoryId = requestEntity.getId();

        // Then
        // 삽입 성공
        Optional<StoryEntity> findResult = storyRepository.findById(insertedStoryId);
        assertThat(findResult).isPresent();

        // version, status는 항상 고정된 값으로 들어감
        StoryEntity insertedEntity = findResult.get();
        assertThat(insertedEntity.getVersion()).isEqualTo(1);
        assertThat(insertedEntity.getStatus()).isEqualTo(StoryStatus.WRITING);
    }
}
