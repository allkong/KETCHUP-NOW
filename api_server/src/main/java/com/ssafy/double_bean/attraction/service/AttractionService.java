package com.ssafy.double_bean.attraction.service;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.common.dto.ListRequestDto;
import com.ssafy.double_bean.common.dto.ListResponseDto;

public interface AttractionService {
    ListResponseDto<AttractionEntity> getAttractionsWithin(ListRequestDto listRequestDto, CoordinateDto leftBottom, CoordinateDto rightTop);
}
