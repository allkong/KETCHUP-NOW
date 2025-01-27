package com.ssafy.double_bean.story_play.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StoryPlayingRepository {
    String SELECT_ALL_SQL = "SELECT sp.id 'sp_id', sp.uuid 'sp_uuid', s.id 's_id', s.uuid 's_uuid', " +
            "u.id 'u_id', u.uuid 'u_uuid', sp.created_at 'sp_created_at', sp.cleared_at sp_cleared_at " +
            "FROM story_playings sp JOIN stories s ON sp.story_id=s.id JOIN users u ON sp.player_id=u.id";

    @Select(SELECT_ALL_SQL)
    @Results(id = "storyPlayingResult", value = {
            @Result(property = "id", column = "sp_id"),
            @Result(property = "uuid", column = "sp_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "storyId", column = "s_id"),
            @Result(property = "storyUuid", column = "s_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "playerId", column = "u_id"),
            @Result(property = "playerUuid", column = "u_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "createdAt", column = "sp_created_at"),
            @Result(property = "clearedAt", column = "sp_cleared_at"),
    })
    List<StoryPlayingEntity> getAll();

    @Select("SELECT EXISTS (SELECT 1 FROM story_playings WHERE player_id=(SELECT id FROM users WHERE uuid=#{userUuid}) AND cleared_at IS NULL)")
    boolean hasPlayingGame(String playerUuid);

    @Insert("INSERT INTO story_playings(story_id, player_id) VALUES (#{storyId}, " +
            "(SELECT id FROM users WHERE uuid=#{playerUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler})" +
            ");")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createGamePlaying(StoryPlayingEntity entity);

    @Select(SELECT_ALL_SQL + " WHERE sp.id=#{id};")
    @ResultMap("storyPlayingResult")
    Optional<StoryPlayingEntity> findById(int id);

    @Select(SELECT_ALL_SQL + " WHERE s.id=#{storyId}")
    @ResultMap("storyPlayingResult")
    List<StoryPlayingEntity> getByStoryId(int storyId);

    @Select(SELECT_ALL_SQL + " WHERE u.uuid=#{playerUuid}")
    @ResultMap("storyPlayingResult")
    List<StoryPlayingEntity> getByPlayerUuid(String playerUuid);

    @Select(SELECT_ALL_SQL + " WHERE u.uuid=#{playerUuid} AND cleared_at IS NULL")
    @ResultMap("storyPlayingResult")
    Optional<StoryPlayingEntity> getCurrentPlaying(String playerUuid);

    @Delete("DELETE FROM story_playings WHERE player_id=(SELECT id FROM users WHERE uuid=#{playerUuid}) AND cleared_at IS NULL")
    void deleteCurrentPlaying(String playerUuid);

    @Update("UPDATE story_playings SET cleared_at=CURRENT_TIMESTAMP WHERE uuid=#{playUuid}")
    void setClearStoryPlaying(String playUuid);

    @Select(SELECT_ALL_SQL + " WHERE sp.uuid=#{uuid}")
    @ResultMap("storyPlayingResult")
    Optional<StoryPlayingEntity> getByUuid(String uuid);
}
