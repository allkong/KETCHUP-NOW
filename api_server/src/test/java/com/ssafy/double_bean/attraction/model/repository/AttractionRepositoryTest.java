package com.ssafy.double_bean.attraction.model.repository;

import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttractionRepositoryTest {
    @Autowired
    private AttractionRepository attractionRepository;

    @Test
    @DisplayName("관광지 목록 정상 조회 테스트")
    void getAll_success() {
        // Given/When
        List<AttractionEntity> attractions = attractionRepository.getAll();
        // Then
        assertThat(attractions).isNotEmpty();
    }
}