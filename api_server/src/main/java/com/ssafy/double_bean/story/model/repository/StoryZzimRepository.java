package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story.model.entity.StoryZzimEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoryZzimRepository {
    String SELECT_ALL_SQL = "SELECT sz.id 'sz_id', sz.uuid 'sz_uuid', s.id 's_id', " +
            "s.uuid 's_uuid', u.id 'u_id', u.uuid 'u_uuid', sz.created_at 'sz_created_at' " +
            "FROM story_zzims sz JOIN stories s ON sz.story_id=s.id JOIN users u ON sz.user_id=u.id ";

    @Select(SELECT_ALL_SQL)
    @Results(id = "storyZzimResult", value = {
            @Result(property = "id", column = "sz_id"),
            @Result(property = "uuid", column = "sz_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "createdAt", column = "sz_created_at"),
            @Result(property = "storyId", column = "s_id"),
            @Result(property = "storyUuid", column = "s_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "userId", column = "u_id"),
            @Result(property = "userUuid", column = "u_uuid", typeHandler = UUIDTypeHandler.class)
    })
    List<StoryZzimEntity> getAll();

    @Select(SELECT_ALL_SQL + " WHERE story_id=(SELECT id FROM stories WHERE uuid=#{storyUuid})")
    @ResultMap("storyZzimResult")
    List<StoryZzimEntity> getZzimsOfStory(String storyUuid);

    @Select(SELECT_ALL_SQL + " WHERE user_id=(SELECT id FROM users WHERE uuid=#{userUuid})")
    @ResultMap("storyZzimResult")
    List<StoryZzimEntity> getZzimsOfUser(String userUuid);

    @Insert("INSERT INTO story_zzims(story_id, user_id) VALUES (#{storyId}, (SELECT id FROM users WHERE uuid=#{userUuid}))")
    void createZzim(int storyId, String userUuid);

    @Delete("DELETE FROM story_zzims WHERE story_id=#{storyId} AND user_id=(SELECT id FROM users WHERE uuid=#{userUuid})")
    void deleteZzim(int storyId, String userUuid);
}
