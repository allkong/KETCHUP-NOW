package com.ssafy.double_bean.story.controller;

import com.ssafy.double_bean.story.dto.StoryReviewCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryReviewResponseDto;
import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;
import com.ssafy.double_bean.story.service.StoryReviewService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final StoryReviewService storyReviewService;
    private final AuthenticatedUser requestedUser;

    public ReviewController(StoryReviewService storyReviewService, AuthenticatedUser requestedUser) {
        this.storyReviewService = storyReviewService;
        this.requestedUser = requestedUser;
    }

    // 해당 스토리 베이스에 연결된 스토리를 클리어한 사용자에 한하여 리뷰를 작성한다.
    @PostMapping("/stories/{story-uuid}/reviews")
    public ResponseEntity<StoryReviewResponseDto> createReview(@PathVariable("story-uuid") UUID storyUuid,
                                                               @Valid @RequestBody StoryReviewCreateRequestDto requestDto) {
        // 리뷰 작성
        StoryReviewEntity createdEntity = storyReviewService.create(storyUuid, requestDto, requestedUser);
        StoryReviewResponseDto dto = StoryReviewResponseDto.fromEntity(createdEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}