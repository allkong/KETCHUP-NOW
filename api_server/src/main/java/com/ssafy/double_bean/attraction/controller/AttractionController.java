package com.ssafy.double_bean.attraction.controller;

import com.ssafy.double_bean.attraction.dto.AreaCodeResponseDto;
import com.ssafy.double_bean.attraction.service.AreaCodeService;
import com.ssafy.double_bean.attraction.util.AreaCode;
import com.ssafy.double_bean.common.dto.ListResponseDto;
import com.ssafy.double_bean.exception.ErrorCode;
import com.ssafy.double_bean.exception.HttpResponseException;
import com.ssafy.double_bean.util.annotation.DocumentOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attractions")
@Tag(name = "Attraction API", description = "관광지 데이터")
public class AttractionController {
    private final AreaCodeService areaCodeService;

    public AttractionController(AreaCodeService areaCodeService) {
        this.areaCodeService = areaCodeService;
    }

    @GetMapping("/areas")
    @Tag(name = "Attraction API")
    @Operation(summary = "대한민국의 지역 분류 코드 및 코드에 대응되는 지역명(서울, 광주, 경남, ...)을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AreaCodeListResponseDto.class)))
    })
    public ResponseEntity<ListResponseDto<AreaCodeResponseDto>> getAreaCodes() {
        List<AreaCode> areaCodes = AreaCode.getAllAreaCodes();
        List<AreaCodeResponseDto> dtos = areaCodes.stream().map(AreaCodeResponseDto::fromEnum).toList();
        ListResponseDto<AreaCodeResponseDto> responseDto = new ListResponseDto<>(1, dtos.size(), false, false, dtos);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/areas/{area-name}")
    @Tag(name = "Attraction API")
    @Operation(summary = "지역별 하위 지역명 목록을 조회하여 반환합니다. (ex, 서울의 하위 지역은 광진구, 성북구, ...)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubAreaNameListResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "알 수 없는 지역명인 경우 (/api/v1/attractions/areas 참고)",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<ListResponseDto<String>> getSubAreaNames(@PathVariable("area-name") String areaName) {
        try {
            List<String> subAreaNames = areaCodeService.getSubAreaNames(areaName);
            ListResponseDto<String> responseDto = new ListResponseDto<>(1, subAreaNames.size(), false, false, subAreaNames);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }
    }

    @DocumentOnly
    private class AreaCodeListResponseDto extends ListResponseDto<AreaCodeResponseDto> {
        public AreaCodeListResponseDto(int page, int size, boolean hasNext, boolean hasPrev, List<AreaCodeResponseDto> data) {
            super(page, size, hasNext, hasPrev, data);
        }
    }

    @DocumentOnly
    private class SubAreaNameListResponseDto extends ListResponseDto<List<String>> {
        public SubAreaNameListResponseDto(int page, int size, boolean hasNext, boolean hasPrev, List<List<String>> data) {
            super(page, size, hasNext, hasPrev, data);
        }
    }
}
