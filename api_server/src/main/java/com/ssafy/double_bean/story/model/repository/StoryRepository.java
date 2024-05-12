package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler;
import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.type_handler.StoryStatusTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface StoryRepository {
    @Select("SELECT s.id, s.uuid. sb.uuid, version, status, title, description, " +
            "sido, gungu, imageUdi, thumbnailImageUdi, createdAt, modifiedAt" +
            " FROM stories s JOIN story_bases sb;")
    @Results(id = "storyResult", value = {
            @Result(property = "id", column = "s.id"),
            @Result(property = "uuid", column = "s.uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "storyBaseUuid", column = "sb.uuid", typeHandler = UUIDTypeHandler.class),
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

    @Select("SELECT * FROM stories WHERE id=#{id}")
    @ResultMap("storyResult")
    Optional<StoryEntity> findById(int id);

    @Select("SELECT * FROM stories WHERE story_base_id=${storyBaseId} AND status='WRITING'")
    @ResultMap("storyResult")
    Optional<StoryEntity> findWritingStoryByStoryBaseId(int storyBaseId);

    @Insert("INSERT INTO story_bases(author_id) VALUES (#{authorId});"
            + "INSERT INTO stories(story_base_id, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri) "
            + "VALUES (LAST_INSERT_ID(), 1, 'WRITING', #{entity.title}, #{entity.description}, #{entity.sido}, #{entity.gungu}, " +
            " #{entity.imageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler}," +
            " #{entity.thumbnailImageUri, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.URITypeHandler});")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id", keyColumn = "id")
    int createFirstStory(int authorId, StoryEntity entity);

    @Select("SELECT s.id, s.uuid, version, status, title, description, sido, gungu, image_uri, thumbnail_udi " +
            "FROM story_bases sb JOIN users u ON sb.author_id=u.id JOIN stories s ON story_bases.id=stories.story_base_id " +
            "WHERE u.uuid=#{authorUuid} AND sb.uuid=#{storyBaseUuid};")
    List<StoryEntity> getStoriesOf(UUID authorUuid, UUID storyBaseUuid);

    List<StoryEntity> getStoryBasesAndWritingStory(UUID requestedUserUuid);
}
