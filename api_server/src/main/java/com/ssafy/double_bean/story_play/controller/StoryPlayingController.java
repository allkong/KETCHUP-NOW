package com.ssafy.double_bean.story_play.controller;

import com.ssafy.double_bean.story_play.dto.StoryPlayingResponseDto;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import com.ssafy.double_bean.story_play.service.StoryPlayingService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StoryPlayingController {
    private final AuthenticatedUser requestedUser;

    private final StoryPlayingService storyPlayingService;

    public StoryPlayingController(AuthenticatedUser requestedUser, StoryPlayingService storyPlayingService) {
        this.requestedUser = requestedUser;
        this.storyPlayingService = storyPlayingService;
    }

    // 스토리 플레이를 시작한다.
    // 단, PUBLISHED 상태인 스토리만 플레이 할 수 있으며,
    // 이미 플레이 중인 스토리가 있는 경우 플레이 할 수 없다.
    @PostMapping("/stories/{story-uuid}/play")
    public ResponseEntity<StoryPlayingResponseDto> startPlayingStory(@PathVariable("story-uuid") UUID storyUuid) {
        StoryPlayingEntity entity = storyPlayingService.startPlaying(storyUuid, requestedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(StoryPlayingResponseDto.fromEntity(entity));
    }
}
