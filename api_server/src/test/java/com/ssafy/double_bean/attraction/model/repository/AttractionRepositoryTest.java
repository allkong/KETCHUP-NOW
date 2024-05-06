package com.ssafy.double_bean.attraction.model.repository;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.common.dto.ListRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
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

    @Test
    @DisplayName("특정 박스 구간 안에 있는 관광지 목록 조회 테스트")
    void getWithin_success() {
        // Given
        // 경복궁 포함
        CoordinateDto leftBottom = new CoordinateDto(37.575335, 126.973634);
        CoordinateDto rightTop = new CoordinateDto(37.583728, 126.980265);
        ListRequestDto requestDto = new ListRequestDto(1, 100);

        // When
        List<AttractionEntity> attractionsWithin = attractionRepository.getWithin(requestDto, leftBottom, rightTop);

        // Then
        List<AttractionEntity> entireAttractions = attractionRepository.getAll();
        assertThat(attractionsWithin)
                .hasSizeLessThan(entireAttractions.size()) // 구획을 좁혔으므로 전체 조회보다 더 적게 나와야 함
                .allMatch(attraction -> attraction.getAreaCode() == 1); // 서울을 조회했으므로 지역 코드는 모두 1
    }
}