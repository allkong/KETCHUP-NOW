package com.ssafy.double_bean.attraction.controller;

import com.ssafy.double_bean.attraction.dto.AreaCodeResponseDto;
import com.ssafy.double_bean.attraction.dto.AttractionResponseDto;
import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.attraction.service.AreaCodeService;
import com.ssafy.double_bean.attraction.service.AttractionService;
import com.ssafy.double_bean.attraction.util.AreaCode;
import com.ssafy.double_bean.common.annotation.DocumentOnly;
import com.ssafy.double_bean.common.dto.ListRequestDto;
import com.ssafy.double_bean.common.dto.ListResponseDto;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attractions")
@Tag(name = "Attraction API", description = "관광지 데이터")
public class AttractionController {
    private final AreaCodeService areaCodeService;
    private final AttractionService attractionService;

    public AttractionController(AreaCodeService areaCodeService, AttractionService attractionService) {
        this.areaCodeService = areaCodeService;
        this.attractionService = attractionService;
    }

    @GetMapping
    @Tag(name = "Attraction API")
    @Operation(summary = "지도 상의 두 지점을 좌하단, 우상단으로 하는 사각형 구역 내부의 여행지 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttractionListResponseDto.class)))
    })
    public ResponseEntity<ListResponseDto<AttractionResponseDto>> getAttractionsBetween(@Valid ListRequestDto listRequestDto,
                                                                                        @Schema(example = "37.57589573217255")
                                                                                        @RequestParam("left-bottom-latitude") double leftBottomLatitude,
                                                                                        @Schema(example = "126.97157440092022")
                                                                                        @RequestParam("left-bottom-longitude") double leftBottomLongitude,
                                                                                        @Schema(example = "37.581898465544334")
                                                                                        @RequestParam("right-top-latitude") double rightTopLatitude,
                                                                                        @Schema(example = "126.98241796105806")
                                                                                        @RequestParam("right-top-longitude") double rightTopLongitude) {
        CoordinateDto leftBottom = new CoordinateDto(leftBottomLatitude, leftBottomLongitude);
        CoordinateDto rightTop = new CoordinateDto(rightTopLatitude, rightTopLongitude);
        ListResponseDto<AttractionEntity> listResponseDto = attractionService.getAttractionsWithin(listRequestDto, leftBottom, rightTop);
        List<AttractionResponseDto> dtoList = listResponseDto.getData()
                .stream().map(AttractionResponseDto::new).toList();
        ListResponseDto<AttractionResponseDto> dto = new ListResponseDto<>(listResponseDto, dtoList);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/areas")
    @Tag(name = "Attraction API")
    @Operation(summary = "대한민국의 지역 분류 코드 및 코드에 대응되는 지역명(서울, 광주, 경남, ...)을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AreaCodeListResponseDto.class)))
    })
    public ResponseEntity<?> getAreaCodes(@RequestParam(value = "area-name", required = false) String areaName) {
        if (areaName == null) {
            List<AreaCode> areaCodes = AreaCode.getAllAreaCodes();
            List<AreaCodeResponseDto> dtos = areaCodes.stream().map(AreaCodeResponseDto::fromEnum).toList();
            ListResponseDto<AreaCodeResponseDto> responseDto = new ListResponseDto<>(1, dtos.size(), false, false, dtos);
            return ResponseEntity.ok(responseDto);
        }
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
    private class AttractionListResponseDto extends ListResponseDto<AttractionResponseDto> {
        public AttractionListResponseDto(int page, int size, boolean hasNext, boolean hasPrev, List<AttractionResponseDto> data) {
            super(page, size, hasNext, hasPrev, data);
        }
    }
}
