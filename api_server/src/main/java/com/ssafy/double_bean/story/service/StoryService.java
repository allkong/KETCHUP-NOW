package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface StoryService {
    StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException, URISyntaxException;

    List<StoryEntity> getStoriesOf(AuthenticatedUser author, UUID storyBaseUuid);

    List<StoryEntity> getStoryBasesAndWritingStory(AuthenticatedUser requestedUser);
}