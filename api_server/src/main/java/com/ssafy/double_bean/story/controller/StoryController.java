package com.ssafy.double_bean.story.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryResponseDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Story API", description = "스토리 관련 API")
public class StoryController {
	@PostMapping("/stories")
	@Tag(name = "Story API")
	@Operation(summary = "새 스토리 베이스와 해당 스토리 베이스의 첫 버전 스토리를 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "생성 성공", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoryResponseDto.class))) })
	public ResponseEntity<StoryResponseDto> createFirstStory(@Valid StoryCreateRequestDto createDto) {
		StoryResponseDto responseDto = new StoryResponseDto(UUID.randomUUID(), 1, StoryStatus.WRITING, "title", "description", "인천", null, null, null, LocalDateTime.now(), null);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
}
