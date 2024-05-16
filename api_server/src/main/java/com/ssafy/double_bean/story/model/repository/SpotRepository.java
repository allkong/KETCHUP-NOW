package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler;
import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story.model.entity.SpotEntity;
import com.ssafy.double_bean.story.model.repository.type_handler.SpotEventTypeTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SpotRepository {
    double ORDER_INDEX_GAP = 100.0;

    String SELECT_ALL_SQL = "SELECT sp.id 'sp_id', sp.uuid 'sp_uuid', latitude, longitude, order_index, " +
            "sp.title 'sp_title', sp.description 'sp_description', sp.image_uri 'sp_image_uri', " +
            "sp.thumbnail_image_uri 'sp_thumbnail_image_uri', sp.created_at 'sp_created_at', sp.modified_at 'sp_modified_at'," +
            "event_type, event_image_uri, event_thumbnail_image_uri, json_event_content " +
            "FROM spots sp";

    @Select(SELECT_ALL_SQL)
    @Results(id = "spots", value = {
            @Result(property = "id", column = "sp_id"),
            @Result(property = "uuid", column = "sp_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "orderIndex", column = "order_index"),
            @Result(property = "title", column = "sp_title"),
            @Result(property = "description", column = "sp_description"),
            @Result(property = "imageUri", column = "sp_image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "thumbnailImageUri", column = "sp_thumbnail_image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "createdAt", column = "sp_created_at"),
            @Result(property = "modifiedAt", column = "sp_modified_at"),
            @Result(property = "eventType", column = "event_type", typeHandler = SpotEventTypeTypeHandler.class),
            @Result(property = "eventImageUri", column = "event_image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "eventThumbnailImageUri", column = "event_thumbnail_image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "jsonEventContent", column = "jsonEventContent")
    })
    List<SpotEntity> getAll();

    @Select(SELECT_ALL_SQL + " WHERE story_id=(SELECT story_id FROM stories WHERE uuid=#{storyUuid})")
    @ResultMap("spots")
    List<SpotEntity> getSpotsOf(String storyUuid);

    @Select(SELECT_ALL_SQL + " WHERE id=#{id}")
    @ResultMap("spots")
    Optional<SpotEntity> findSpotById(int id);

    @Insert("INSERT INTO spots(story_id, latitude, longitude, order_index, title, description, image_uri, thumbnail_image_uri) " +
            "VALUES ((SELECT id FROM stories WHERE uuid=#{storyUuid}), #{entity.latitude}, #{entity.longitude}, " +
            "#{entity.orderIndex}, #{entity.title}, #{entity.description}, " +
            "#{entity.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "#{entity.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler});")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id", keyColumn = "id")
    void insertNewSpot(String storyUuid, SpotEntity entity);

    @Insert({"<script>",
            "INSERT INTO spots(story_id, latitude, longitude, order_index, title, description, image_uri, thumbnail_image_uri) ",
            "VALUES ",
            "<foreach collection='entities' item='entity' open='(' separator='), (' close=')'>",
            "(SELECT id FROM stories WHERE uuid=#{storyUuid}), #{entity.latitude}, #{entity.longitude}, ",
            "#{entity.orderIndex}, #{entity.title}, #{entity.description}, ",
            "#{entity.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, ",
            "#{entity.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}",
            "</foreach>",
            "</script>"})
    void insertBulk(String storyUuid, List<SpotEntity> entities);

    @Update("UPDATE spots SET latitude=#{dto.latitude}, longitude=#{dto.longitude}, order_index=#{dto.orderIndex}, " +
            "title=#{dto.title}, description=#{dto.description}, modified_at=CURRENT_TIMESTAMP, " +
            "event_type=#{dto.eventType, typeHandler=com.ssafy.double_bean.story.model.repository.type_handler.SpotEventTypeTypeHandler}, " +
            "image_uri=#{dto.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "thumbnail_image_uri=#{dto.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "event_image_uri=#{dto.eventImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "event_thumbnail_image_uri=#{dto.eventThumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler} " +
            "WHERE id=#{targetId}")
    void updateSpot(int targetId, SpotEntity dto);

    @Delete("DELETE FROM spots WHERE id=#{id}")
    void deleteSpot(int id);
}
