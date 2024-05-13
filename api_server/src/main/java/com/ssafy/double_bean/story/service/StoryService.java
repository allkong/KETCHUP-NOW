package com.ssafy.double_bean.story.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;

public interface StoryService {
    StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException, URISyntaxException;

	List<StoryEntity> getStoryBaseAndLatestStory(AuthenticatedUser requestedUser);

	List<StoryEntity> getStoriesOf(UUID storyBaseUuid, AuthenticatedUser requestedUser);

	StoryEntity getStory(UUID storyBaseUuid, UUID storyUuid, AuthenticatedUser requestedUser);
}
