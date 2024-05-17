package com.ssafy.double_bean.story.controller;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.StoryReviewCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryReviewResponseDto;
import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;
import com.ssafy.double_bean.story.service.StoryReviewService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // 해당 스토리에 연결된 스토리를 클리어한 사용자에 한하여 리뷰를 작성한다.
    @PostMapping("/stories/{story-uuid}/reviews")
    public ResponseEntity<StoryReviewResponseDto> createReview(@PathVariable("story-uuid") UUID storyUuid,
                                                               @Valid @RequestBody StoryReviewCreateRequestDto requestDto) {
        // 리뷰 작성
        StoryReviewEntity createdEntity = storyReviewService.create(storyUuid, requestDto, requestedUser);
        StoryReviewResponseDto dto = StoryReviewResponseDto.fromEntity(createdEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // 해당 스토리에 연결된 리뷰 목록을 조회한다.
    @GetMapping("/stories/{story-uuid}/reviews")
    public ResponseEntity<List<StoryReviewResponseDto>> getReviewsOf(@PathVariable("story-uuid") UUID storyUuid) {
        List<StoryReviewEntity> entities = storyReviewService.getReviewsOf(storyUuid);
        List<StoryReviewResponseDto> dtos = entities.stream().map(StoryReviewResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 특정 리뷰를 조회한다.
    @GetMapping("/reviews/{review-uuid}")
    public ResponseEntity<StoryReviewResponseDto> getReview(@PathVariable("review-uuid") UUID reviewUuid) {
        StoryReviewEntity entity = storyReviewService.getReview(reviewUuid)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));
        StoryReviewResponseDto dto = StoryReviewResponseDto.fromEntity(entity);
        return ResponseEntity.ok(dto);
    }

    // 요청한 사용자의 리뷰 목록을 조회한다.
    @GetMapping("/reviews/me")
    public ResponseEntity<List<StoryReviewResponseDto>> getMyReviews() {
        List<StoryReviewEntity> entities = storyReviewService.getReviewsWrittenBy(requestedUser);
        List<StoryReviewResponseDto> dtos = entities.stream().map(StoryReviewResponseDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // 작성한 리뷰를 수정한다.
    @PutMapping("/reviews/{review-uuid}")
    public ResponseEntity<StoryReviewResponseDto> updateReview(@PathVariable("review-uuid") UUID reviewUuid, @Valid @RequestBody StoryReviewCreateRequestDto requestDto) {
        StoryReviewEntity entity = storyReviewService.updateReview(reviewUuid, requestDto, requestedUser);
        StoryReviewResponseDto dto = StoryReviewResponseDto.fromEntity(entity);
        return ResponseEntity.ok(dto);
    }

    // 작성한 리뷰를 삭제한다.
    @DeleteMapping("/reviews/{review-uuid}")
    public ResponseEntity<StoryReviewResponseDto> deleteReview(@PathVariable("review-uuid") UUID reviewUuid) {
        storyReviewService.deleteReview(reviewUuid, requestedUser);
        return ResponseEntity.noContent().build();
    }
}