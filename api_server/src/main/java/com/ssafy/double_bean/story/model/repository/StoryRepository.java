package com.ssafy.double_bean.story.model.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.util.type_handler.URITypeHandler;
import com.ssafy.double_bean.util.type_handler.UUIDTypeHandler;

@Mapper
public interface StoryRepository {
	@Select("SELECT * FROM stories")
	@Results(id = "storyResult", value= {
			@Result(property = "id", column = "id"),
			@Result(property = "uuid", column = "uuid", typeHandler = UUIDTypeHandler.class),
			@Result(property = "version", column = "version"),
			@Result(property = "status", column = "status"),
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
	
	@Insert("INSERT INTO story_bases(author_id) VALUES (#{authorId});"
			+ "INSERT INTO stories(story_base_id, version, status, title, description, sido, gungu, image_uri, thumbnail_image_uri) "
			+ "VALUES (LAST_INSERT_ID(), 1, 'WRITING', #{entity.title}, #{entity.description}, #{entity.sido}, #{entity.gungu}, '', '');")
	@Options(useGeneratedKeys = true, keyProperty = "entity.id", keyColumn = "id")
	public int createFirstStory(int authorId, StoryEntity entity);
}
