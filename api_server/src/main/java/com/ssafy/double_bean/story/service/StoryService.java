package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.attraction.dto.CoordinateDto;
import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryUpdateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryZzimEntity;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface StoryService {
    StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException, URISyntaxException;

    List<StoryEntity> getStoryBaseAndLatestStory(AuthenticatedUser requestedUser);

    StoryEntity setPresignedUriFields(StoryEntity entity);

    List<StoryEntity> getStoriesOf(UUID storyBaseUuid, AuthenticatedUser requestedUser);

    StoryEntity getStory(UUID storyUuid, AuthenticatedUser requestedUser);

    StoryEntity updateStory(UUID storyUuid, AuthenticatedUser requestedUser,
                            StoryUpdateRequestDto updateDto) throws IOException, URISyntaxException;

    void deleteStory(UUID storyUuid, AuthenticatedUser requestedUser);

    StoryEntity duplicateStory(UUID storyUuid, AuthenticatedUser requestedUser) throws URISyntaxException;

    List<StoryEntity> getStoriesWithin(CoordinateDto leftBottom, CoordinateDto rightBottom, String sido, String gungu);

    List<StoryZzimEntity> getZzimsOfStory(UUID storyUuid);

    List<StoryZzimEntity> getZzimsOfUser(AuthenticatedUser requestedUser);

    boolean toggleZzim(UUID storyUuid, AuthenticatedUser requestedUser);
}
