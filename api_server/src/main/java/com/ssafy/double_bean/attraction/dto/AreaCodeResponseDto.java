package com.ssafy.double_bean.attraction.dto;

import com.ssafy.double_bean.attraction.util.AreaCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지역 이름/코드 쌍")
public record AreaCodeResponseDto(
        @Schema(description = "지역명")
        String name,
        @Schema(description = "고유 코드")
        int code) {
    public static AreaCodeResponseDto fromEnum(AreaCode areaCode) {
        return new AreaCodeResponseDto(areaCode.getName(), areaCode.getCode());
    }
}
