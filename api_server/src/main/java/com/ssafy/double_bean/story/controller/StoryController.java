package com.ssafy.double_bean.story.controller;

import com.ssafy.double_bean.aws.s3.S3Client;
import com.ssafy.double_bean.story.dto.StoryBaseResponseDto;
import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryResponseDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.service.StoryService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    private final S3Client s3Client;
    private final StoryService storyService;
    private final AuthenticatedUser requestedUser;

    public StoryController(S3Client s3Client, StoryService storyService, AuthenticatedUser requestedUser) {
        this.s3Client = s3Client;
        this.storyService = storyService;
        this.requestedUser = requestedUser;
    }

    @PostMapping("/stories")
    @Tag(name = "Story API")
    @Operation(summary = "새 스토리 베이스와 해당 스토리 베이스의 첫 버전 스토리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = StoryResponseDto.class)))})
    public ResponseEntity<StoryResponseDto> createFirstStory(@Valid StoryCreateRequestDto createDto, @RequestPart MultipartFile imageFile) throws IOException, URISyntaxException {
        StoryEntity createdStory = storyService.createFirstStory(requestedUser, createDto, imageFile);
        StoryResponseDto dto = createdStory.toResponseDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/story-bases")
    @Tag(name = "Story API")
    @Operation(summary = "생성한 스토리 베이스 목록과 각 스토리 베이스의 WRITING 상태인 스토리를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StoryBaseResponseDto.class))))
    })
    public ResponseEntity<List<StoryBaseResponseDto>> getStoryBasesOf() {
        List<StoryEntity> resultList = storyService.getStoryBasesAndWritingStory(requestedUser);
        return null;
    }

    @GetMapping("/stories/{story-base-uuid}")
    @Tag(name = "Story API")
    @Operation(summary = "특정 스토리 베이스의 하위 스토리 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StoryResponseDto.class))))
    })
    public ResponseEntity<List<StoryResponseDto>> getStoriesOf(@PathVariable("story-base-uuid") UUID storyBaseUuid) {
        List<StoryEntity> resultList = storyService.getStoriesOf(requestedUser, storyBaseUuid);
        List<StoryResponseDto> responseDtoList = resultList.stream()
                .map(e -> {
                    if (e.getImageUri() != null) {
                        e.setImageUri(s3Client.getPresignedUri(e.getImageUri()));
                    }
                    if (e.getThumbnailImageUri() != null) {
                        e.setThumbnailImageUri(s3Client.getPresignedUri(e.getThumbnailImageUri()));
                    }
                    return e.toResponseDto();
                }).toList();
        return ResponseEntity.ok(responseDtoList);
    }
}
