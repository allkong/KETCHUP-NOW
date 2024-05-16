package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler;
import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.type_handler.StoryStatusTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StoryRepository {
    String SELECT_ALL_SQL = "SELECT s.id, s.uuid, sb.uuid AS sb_uuid, u.nickname AS author_nickname, "
            + "u.uuid AS author_uuid, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri, s.created_at, s.modified_at "
            + "FROM stories s JOIN story_bases sb ON s.story_base_id=sb.id JOIN users u ON sb.author_id=u.id";

    @Select(SELECT_ALL_SQL)
    @Results(id = "storyResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "uuid", column = "uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "storyBaseUuid", column = "sb_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "authorNickname", column = "author_nickname"),
            @Result(property = "authorUuid", column = "author_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "version", column = "version"),
            @Result(property = "status", column = "status", typeHandler = StoryStatusTypeHandler.class),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "sido", column = "sido"),
            @Result(property = "gungu", column = "gungu"),
            @Result(property = "imageUri", column = "image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "thumbnailImageUri", column = "thumbnail_image_uri", typeHandler = URITypeHandler.class),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "modifiedAt", column = "modified_at"),
    })
    List<StoryEntity> getAll();

    @Select(SELECT_ALL_SQL + " WHERE s.id=#{id}")
    @ResultMap("storyResult")
    Optional<StoryEntity> findById(int id);

    @Select(SELECT_ALL_SQL + " WHERE story_base_id=${storyBaseId} AND status='WRITING'")
    @ResultMap("storyResult")
    Optional<StoryEntity> findWritingStoryByStoryBaseId(int storyBaseId);

    @Insert("INSERT INTO story_bases(author_id) VALUES (#{authorId});"
            + "INSERT INTO stories(story_base_id, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri) "
            + "VALUES (LAST_INSERT_ID(), 1, 'WRITING', #{entity.title}, #{entity.description}, #{entity.sido}, #{entity.gungu}, " +
            " #{entity.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}," +
            " #{entity.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler});")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id", keyColumn = "id")
    int createFirstStory(int authorId, StoryEntity entity);

    @Insert("INSERT INTO stories(story_base_id, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri) "
            + "VALUES (" +
            "(SELECT id FROM story_bases WHERE uuid=#{storyBaseUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler}), " +
            "#{version}, 'WRITING', #{title}, #{description}, #{sido}, #{gungu}, " +
            " #{imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}," +
            " #{thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler});")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createStory(StoryEntity entity);

    // 스토리 베이스별 최신 스토리만 들고 와서 join 해주는 방식
    @Select("WITH s AS ("
            + "SELECT * "
            + "FROM stories WHERE (story_base_id, created_at) IN (SELECT story_base_id, MAX(created_at) AS created_at FROM stories GROUP BY story_base_id)"
            + ")" + SELECT_ALL_SQL + " AND u.uuid=#{authorUuid}")
    @ResultMap("storyResult")
    List<StoryEntity> getLatestStoriesOf(String authorUuid);

    // 스토리 베이스에 연결된 모든 하위 스토리를 조회
    @Select(SELECT_ALL_SQL + " WHERE sb.uuid=#{storyBaseUuid} AND u.uuid=#{requestedUserUuid}")
    @ResultMap("storyResult")
    List<StoryEntity> getSubStoriesOf(String storyBaseUuid, String requestedUserUuid);

    // 스토리 단건 조회
    @Select(SELECT_ALL_SQL + " WHERE s.uuid=#{storyUuid}")
    @ResultMap("storyResult")
    Optional<StoryEntity> findByUuid(String storyUuid);

    @Update("UPDATE stories SET status=#{updated.status}, title=#{updated.title}, description=#{updated.description}, " +
            "sido=#{updated.sido}, gungu=#{updated.gungu}, " +
            "image_uri=#{updated.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "thumbnail_image_uri=#{updated.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}, " +
            "modified_at=CURRENT_TIMESTAMP " +
            "WHERE uuid=#{storyUuid}")
    void updateStory(String storyUuid, StoryEntity updated);

    @Select(SELECT_ALL_SQL + " WHERE s.uuid=#{storyUuid}")
    @ResultMap("storyResult")
    Optional<StoryEntity> getStoryByUuid(String storyUuid);

    @Delete("DELETE FROM stories WHERE id=#{id}")
    void deleteById(int id);

    @Select({
            "WITH in_bound_bases AS (",
            "   SELECT story_base_id FROM spots JOIN stories ON spots.story_id=stories.id",
            "   WHERE",
            "   (",
            "       (latitude BETWEEN #{leftBottom.latitude} AND #{rightTop.latitude})",
            "       AND",
            "       (longitude BETWEEN #{leftBottom.longitude} AND #{rightTop.longitude})",
            "   )",
            "   AND",
            "   (status='PUBLISHED')",
            "   GROUP BY story_base_id",
            ")",
            "SELECT s.id, s.uuid, sb.uuid AS sb_uuid, u.nickname AS author_nickname, ",
            "u.uuid AS author_uuid, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri, s.created_at, s.modified_at ",
            "FROM stories s",
            "JOIN in_bound_bases ON in_bound_bases.story_base_id=s.story_base_id ",
            "JOIN story_bases sb ON s.story_base_id=sb.id ",
            "JOIN users u ON sb.author_id=u.id ",
            "WHERE version=",
            "(",
            "   SELECT MAX(version)",
            "   FROM stories",
            "   WHERE story_base_id=in_bound_bases.story_base_id AND status='PUBLISHED'",
            ")"
    })
    @ResultMap("storyResult")
    List<StoryEntity> getStoriesWithin(CoordinateDto leftBottom, CoordinateDto rightTop);
}
