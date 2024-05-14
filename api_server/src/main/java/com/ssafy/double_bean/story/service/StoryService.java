package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryUpdateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface StoryService {
    StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException, URISyntaxException;

    List<StoryEntity> getStoryBaseAndLatestStory(AuthenticatedUser requestedUser);

    List<StoryEntity> getStoriesOf(UUID storyBaseUuid, AuthenticatedUser requestedUser);

    StoryEntity getStory(UUID storyUuid, AuthenticatedUser requestedUser);

    StoryEntity updateStory(UUID storyUuid, AuthenticatedUser requestedUser,
                            StoryUpdateRequestDto updateDto, MultipartFile newImage) throws IOException, URISyntaxException;
}
