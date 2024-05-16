package com.ssafy.double_bean.story_play.controller;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story_play.dto.StoryPlayingResponseDto;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import com.ssafy.double_bean.story_play.service.StoryPlayingService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // 플레이 했던, 플레이 중인 플레이 기록을 모두 가져온다.
    @GetMapping("/playings")
    public ResponseEntity<List<StoryPlayingResponseDto>> getPlayings() {
        List<StoryPlayingEntity> entities = storyPlayingService.getStoryPlayingsOf(requestedUser);
        List<StoryPlayingResponseDto> dtos = entities.stream().map(StoryPlayingResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 현재 플레이 중인 기록을 가져온다.
    @GetMapping("/playings/now")
    public ResponseEntity<StoryPlayingResponseDto> getCurrentPlaying() {
        StoryPlayingEntity entity = storyPlayingService.getPlayingOne(requestedUser)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));
        StoryPlayingResponseDto dto = StoryPlayingResponseDto.fromEntity(entity);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/playings/now")
    public ResponseEntity<Void> deleteCurrentPlaying() {
        storyPlayingService.deleteCurrentPlay(requestedUser);
        return ResponseEntity.noContent().build();
    }
}
