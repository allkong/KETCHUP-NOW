package com.ssafy.double_bean.attraction.model.repository;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.common.dto.ListRequestDto;
import com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

import static com.ssafy.double_bean.common.model.repository.RepositoryUtil.PAGING_QUERY;

@Mapper
public interface AttractionRepository {
    @Select("SELECT id, title, addr1, addr2, area_code, category_1, category_2, category_3,"
            + " content_type_id, first_image, second_image, latitude, longitude, sigungucode, tel"
            + " FROM attractions")
    @Results(id = "attractionResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "address", column = "addr1"),
            @Result(property = "detailAddress", column = "addr2"),
            @Result(property = "areaCode", column = "area_code"),
            @Result(property = "category1", column = "category_1"),
            @Result(property = "category2", column = "category_2"),
            @Result(property = "category3", column = "category_3"),
            @Result(property = "contentTypeId", column = "content_type_id"),
            @Result(property = "firstImageUrl", column = "first_image", typeHandler = URITypeHandler.class),
            @Result(property = "secondImageUrl", column = "second_image", typeHandler = URITypeHandler.class),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "sigunguCode", column = "sigungucode"),
            @Result(property = "telephoneNumber", column = "tel"),
    })
    List<AttractionEntity> getAll();

    @Select("SELECT * FROM attractions WHERE (latitude BETWEEN #{leftBottom.latitude} AND #{rightTop.latitude}) " +
            "AND (longitude BETWEEN #{leftBottom.longitude} AND #{rightTop.longitude}) " + PAGING_QUERY)
    @ResultMap("attractionResult")
    List<AttractionEntity> getWithin(ListRequestDto listRequest, CoordinateDto leftBottom, CoordinateDto rightTop);
}
