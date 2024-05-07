package com.ssafy.double_bean.story.controller;

import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryResponseDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Story API", description = "스토리 관련 API")
public class StoryController {
    private final StoryService storyService;
    private final AuthenticatedUser requestedUser;

    public StoryController(StoryService storyService, AuthenticatedUser requestedUser) {
        this.storyService = storyService;
        this.requestedUser = requestedUser;
    }

    @PostMapping("/stories")
    @Tag(name = "Story API")
    @Operation(summary = "새 스토리 베이스와 해당 스토리 베이스의 첫 버전 스토리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoryResponseDto.class)))})
    public ResponseEntity<StoryResponseDto> createFirstStory(@Valid StoryCreateRequestDto createDto) {
        StoryEntity createdStory = storyService.createFirstStory(requestedUser, createDto);
        StoryResponseDto dto = createdStory.toResponseDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
