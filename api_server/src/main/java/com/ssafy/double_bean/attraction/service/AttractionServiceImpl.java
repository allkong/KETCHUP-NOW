package com.ssafy.double_bean.attraction.service;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.attraction.model.repository.AttractionRepository;
import com.ssafy.double_bean.common.dto.ListRequestDto;
import com.ssafy.double_bean.common.dto.ListResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ssafy.double_bean.common.model.repository.RepositoryUtil.getRequestedPage;

@Service
public class AttractionServiceImpl implements AttractionService {
    private final AttractionRepository attractionRepository;

    public AttractionServiceImpl(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    @Override
    public ListResponseDto<AttractionEntity> getAttractionsWithin(ListRequestDto listRequestDto, CoordinateDto leftBottom, CoordinateDto rightTop) {
        List<AttractionEntity> entities = attractionRepository.getWithin(listRequestDto, leftBottom, rightTop);
        return getRequestedPage(listRequestDto, entities);
    }
}
