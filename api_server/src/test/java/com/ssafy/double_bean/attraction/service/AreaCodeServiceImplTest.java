package com.ssafy.double_bean.attraction.service;

import com.ssafy.double_bean.attraction.util.AreaCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=dev")
class AreaCodeServiceImplTest {
    @Autowired
    private AreaCodeService areaCodeService;

    @Test
    @DisplayName("HTTP 요청 기반 area code 받아오기")
    void getSubAreaNames_success() {
        // Given
        AreaCode seoul = AreaCode.SEOUL;

        // When
        List<String> resultByCode = areaCodeService.getSubAreaNames(seoul.getCode());
        List<String> resultByName = areaCodeService.getSubAreaNames(seoul.getName());

        // Then
        assertThat(resultByCode)
                .hasSizeGreaterThan(0)
                .hasSameSizeAs(resultByName); // 이름이든 코드든 동일한 결과가 조회되어야 함
    }
}