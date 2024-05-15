package com.ssafy.double_bean.story.controller;

import com.ssafy.double_bean.story.dto.*;
import com.ssafy.double_bean.story.model.entity.SpotEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.service.SpotService;
import com.ssafy.double_bean.story.service.StoryService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Story API", description = "스토리 관련 API")
public class StoryController {
    private final StoryService storyService;
    private final SpotService spotService;
    private final AuthenticatedUser requestedUser;

    public StoryController(StoryService storyService, SpotService spotService, AuthenticatedUser requestedUser) {
        this.storyService = storyService;
        this.spotService = spotService;
        this.requestedUser = requestedUser;
    }

    @PostMapping("/stories")
    @Tag(name = "Story API")
    @Operation(summary = "새 스토리 베이스와 해당 스토리 베이스의 첫 버전 스토리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = StoryResponseDto.class)))})
    public ResponseEntity<StoryResponseDto> createFirstStory(@Valid StoryCreateRequestDto createDto, @RequestPart(required = false) MultipartFile imageFile) throws IOException, URISyntaxException {
        StoryEntity createdStory = storyService.createFirstStory(requestedUser, createDto, imageFile);
        StoryResponseDto dto = createdStory.toResponseDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // 요청한 사용자의 최신 버전 스토리 목록을 가져온다.
    // 즉, 스토리 베이스의 개수와 같아야 한다.
    @GetMapping("/story-bases")
    public ResponseEntity<List<StoryResponseDto>> getLatestStories() {
        List<StoryEntity> entities = storyService.getStoryBaseAndLatestStory(requestedUser);
        List<StoryResponseDto> dtos = entities.stream().map(StoryResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 주어진 스토리 베이스의 모든 하위 스토리 목록을 가져온다.
    @GetMapping("/story-bases/{story-base-uuid}/stories")
    public ResponseEntity<List<StoryResponseDto>> getStoriesOf(@PathVariable("story-base-uuid") UUID storyBaseUuid) {
        List<StoryEntity> entities = storyService.getStoriesOf(storyBaseUuid, requestedUser);
        List<StoryResponseDto> dtos = entities.stream().map(StoryResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 주어진 UUID를 가지는 스토리를 가져온다.
    // 단, PUBLISHED 되지 않은 경우 소유자만 조회할 수 있다.
    @GetMapping("/stories/{story-uuid}")
    public ResponseEntity<StoryResponseDto> getStory(@PathVariable("story-uuid") UUID storyUuid) {
        StoryEntity entity = storyService.getStory(storyUuid, requestedUser);
        StoryResponseDto dto = StoryResponseDto.fromEntity(entity);
        return ResponseEntity.ok(dto);
    }

    // 주어진 UUID를 가지는 스토리의 정보를 업데이트 한다.
    // 단, status가 PUBLISHED인 경우 수정할 수 없다.
    @PutMapping("/stories/{story-uuid}")
    public ResponseEntity<StoryResponseDto> getStory(
            @PathVariable("story-uuid") UUID storyUuid,
            @Valid StoryUpdateRequestDto updateDto, @RequestPart(required = false) MultipartFile imageFile) throws IOException, URISyntaxException {
        StoryEntity updatedEntity = storyService.updateStory(storyUuid, requestedUser, updateDto, imageFile);
        return ResponseEntity.ok(StoryResponseDto.fromEntity(updatedEntity));
    }

    @GetMapping("/stories/{story-uuid}/spots")
    public ResponseEntity<List<SpotResponseDto>> getSpotsOf(@PathVariable("story-uuid") UUID storyUuid) {
        List<SpotEntity> entities = spotService.getSpotsOf(storyUuid, requestedUser);
        List<SpotResponseDto> dtos = entities.stream().map(SpotResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 지정한 위치에 spot을 추가한다.
    // 단, 스토리의 상태가 WRITING일 때에만 가능하다.
    @PostMapping("/stories/{story-uuid}/spots")
    public ResponseEntity<SpotResponseDto> insertSpotTo(@PathVariable("story-uuid") UUID storyUuid,
                                                        @Valid SpotInsertRequestDto requestDto,
                                                        @RequestParam(required = false) MultipartFile imageFile) throws IOException {
        SpotEntity insertedEntity = spotService.insertSpotTo(storyUuid, requestDto, imageFile, requestedUser);
        SpotResponseDto dto = SpotResponseDto.fromEntity(insertedEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // 특정 스팟에 대한 정보를 수정한다.
    // 단, 스토리의 상태가 WRITING일 때에만 가능하다.
    @PutMapping("/stories/{story-uuid}/spots/{spot-uuid}")
    public ResponseEntity<SpotResponseDto> updateSpot(@PathVariable("story-uuid") UUID storyUuid,
                                                      @PathVariable("spot-uuid") UUID spotUuid,
                                                      @Valid SpotUpdateRequestDto requestDto,
                                                      @RequestParam(required = false) MultipartFile spotImageFile,
                                                      @RequestParam(required = false) MultipartFile eventImageFile) throws IOException {
        SpotEntity updatedEntity = spotService.updateSpot(storyUuid, spotUuid, requestDto, spotImageFile, eventImageFile, requestedUser);
        SpotResponseDto dto = SpotResponseDto.fromEntity(updatedEntity);
        return ResponseEntity.ok(dto);
    }

    // 특정 스팟을 삭제한다.
    // 단, 스토리의 상태가 WRITING일 때에만 가능하다.
    @DeleteMapping("/stories/{story-uuid}/spots/{spot-uuid}")
    public ResponseEntity<SpotResponseDto> updateSpot(@PathVariable("story-uuid") UUID storyUuid,
                                                      @PathVariable("spot-uuid") UUID spotUuid) {
        spotService.delteSpot(storyUuid, spotUuid, requestedUser);
        return ResponseEntity.noContent().build();
    }
}
