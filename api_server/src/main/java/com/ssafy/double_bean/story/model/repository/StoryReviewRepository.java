package com.ssafy.double_bean.story.model.repository;

import com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler;
import com.ssafy.double_bean.story.dto.StoryReviewCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StoryReviewRepository {
    String SELECT_ALL_SQL = "SELECT sr.id 'sr_id', sr.uuid 'sr_uuid', s.uuid 's_uuid', u.uuid 'u_uuid', s.title 's_title', " +
            "nickname, sr.title 'sr_title', sr.content 'sr_content', sr.score 'sr_score', sr.created_at 'sr_created_at', " +
            "sr.modified_at 'sr_modified_at' " +
            "FROM story_reviews sr JOIN stories s ON sr.story_id=s.id JOIN users u ON sr.user_id=u.id ";

    @Select(SELECT_ALL_SQL)
    @Results(id = "storyReviewResult", value = {
            @Result(property = "id", column = "sr_id"),
            @Result(property = "uuid", column = "sr_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "storyUuid", column = "s_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "storyTitle", column = "s_title"),
            @Result(property = "userUuid", column = "u_uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "userNickname", column = "nickname"),
            @Result(property = "title", column = "sr_title"),
            @Result(property = "content", column = "sr_content"),
            @Result(property = "score", column = "sr_score"),
            @Result(property = "createdAt", column = "sr_created_at"),
            @Result(property = "modifiedAt", column = "sr_modified_at")
    })
    List<StoryReviewEntity> getAll();

    @Select(SELECT_ALL_SQL + " WHERE sr.id=#{id}")
    @ResultMap("storyReviewResult")
    Optional<StoryReviewEntity> getById(int id);

    @Select({
            SELECT_ALL_SQL,
            "WHERE u.id=(SELECT id FROM users WHERE uuid=#{userUuid}) ",
            "ORDER BY created_at DESC"
    })
    @ResultMap("storyReviewResult")
    List<StoryReviewEntity> getStoryReviewsWrittenBy(String userUuid);

    @Insert({
            "INSERT INTO story_reviews(story_id, user_id, title, content, score) ",
            "VALUES (",
            "(SELECT id FROM stories WHERE uuid=#{storyUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler}), ",
            "(SELECT id FROM users WHERE uuid=#{userUuid, typeHandler=com.ssafy.double_bean.common.model.repository.type_handler.UUIDTypeHandler}), ",
            "#{title}, #{content}, #{score}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createStoryReview(StoryReviewEntity dto);

    @Select(SELECT_ALL_SQL + "WHERE s.id=(SELECT id FROM stories WHERE uuid=#{storyUuid})")
    @ResultMap("storyReviewResult")
    List<StoryReviewEntity> getStoryReviewsOf(String storyUuid);

    @Select(SELECT_ALL_SQL + "WHERE sr.uuid=#{reviewUuid}")
    @ResultMap("storyReviewResult")
    Optional<StoryReviewEntity> getReviewByUuid(String reviewUuid);

    @Update("UPDATE story_reviews SET title=#{dto.title}, content=#{dto.content}, score=#{dto.score} WHERE uuid=#{reviewUuid}")
    void update(String reviewUuid, StoryReviewCreateRequestDto dto);

    @Delete("DELETE FROM story_reviews WHERE uuid=#{reviewUuid}")
    void delete(String reviewUuid);
}
