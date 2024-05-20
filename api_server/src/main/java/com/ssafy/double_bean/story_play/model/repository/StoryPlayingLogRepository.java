package com.ssafy.double_bean.story_play.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingLogEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StoryPlayingLogRepository {
    String SELECT_ALL_SQL = "SELECT l.id 'l_id', l.uuid 'l_uuid', l.created_at 'l_created_at', l.json_event_content 'l_json_event_content', " +
            "sp.id 'sp_id', sp.uuid 'sp_uuid', s.id 's_id', s.uuid 's_uuid', u.id 'u_id', u.uuid 'u_uuid' " +
            "FROM story_playing_logs l JOIN story_playings sp ON l.story_playing_id=sp.id " +
            "JOIN spots s ON l.spot_id=s.id JOIN users u ON l.user_id=u.id";

    @Select(SELECT_ALL_SQL)
    @Results(id = "storyPlayingLogResult", value = {
            @Result(property = "id", column = "l_id"),
            @Result(property = "uuid", column = "l_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "createdAt", column = "l_created_at"),
            @Result(property = "jsonEventContent", column = "l_json_event_content"),
            @Result(property = "storyPlayingId", column = "sp_id"),
            @Result(property = "storyPlayingUuid", column = "sp_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "spotId", column = "s_id"),
            @Result(property = "spotUuid", column = "s_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "userId", column = "u_id"),
            @Result(property = "userUuid", column = "u_uuid", typeHandler = UUIDTypeHandler.class),
    })
    List<StoryPlayingLogEntity> getAll();

    @Insert("INSERT INTO story_playing_logs(user_id, story_playing_id, spot_id) " +
            "VALUES(" +
            "(SELECT id FROM users WHERE uuid=#{userUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler}), " +
            "(SELECT id FROM story_playings WHERE uuid=#{storyPlayingUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler}), " +
            "(SELECT id FROM spots WHERE uuid=#{spotUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler})" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(StoryPlayingLogEntity dto);

    @Select(SELECT_ALL_SQL + " WHERE story_playing_id=#{playingId}")
    @ResultMap("storyPlayingLogResult")
    List<StoryPlayingLogEntity> getStoryPlayingLogsUnder(int playingId);

    @Select(SELECT_ALL_SQL + " WHERE l.id=#{id}")
    @ResultMap("storyPlayingLogResult")
    Optional<StoryPlayingLogEntity> getById(int id);
}
