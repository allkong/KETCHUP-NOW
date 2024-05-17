package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.StoryReviewCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;
import com.ssafy.double_bean.story.model.repository.StoryReviewRepository;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import com.ssafy.double_bean.story_play.service.StoryPlayingService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoryReviewService {
    private final StoryReviewRepository storyReviewRepository;
    private final StoryPlayingService storyPlayingService;

    public StoryReviewService(StoryPlayingService storyPlayingService, StoryReviewRepository storyReviewRepository) {
        this.storyPlayingService = storyPlayingService;
        this.storyReviewRepository = storyReviewRepository;
    }

    public List<StoryReviewEntity> getAll() {
        return storyReviewRepository.getAll();
    }

    @Transactional
    public StoryReviewEntity create(UUID storyUuid, StoryReviewCreateRequestDto requestDto, AuthenticatedUser requestedUser) {
        // 요청한 사용자가 스토리를 클리어한 적이 있는지 확인
        List<StoryPlayingEntity> plays = storyPlayingService.getStoryPlayingsOf(requestedUser);

        // 클리어한 기록 중에서 검색
        StoryPlayingEntity playing = plays.stream().filter(p -> p.getStoryUuid().equals(storyUuid))
                .findFirst().orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 아직 클리어하지 않은 경우 리뷰 작성 불가
        if (playing.getClearedAt() == null) {
            throw new HttpResponseException(ErrorCode.NOT_CLEARED_STORY);
        }

        // 이미 리뷰를 작성한 경우 작성 불가
        getReviewsWrittenBy(requestedUser).stream()
                .filter(sr -> sr.getStoryUuid().equals(storyUuid))
                .findFirst().ifPresent((review) -> {
                    throw new HttpResponseException(ErrorCode.ALREADY_REVIEWED_STORY);
                });

        // 이외의 경우 리뷰 작성 가능
        StoryReviewEntity requestEntity = requestDto.toRequestEntity(storyUuid, requestedUser.getUuid());
        storyReviewRepository.createStoryReview(requestEntity);

        return storyReviewRepository.getById(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create story review."));
    }

    public List<StoryReviewEntity> getReviewsWrittenBy(AuthenticatedUser requestedUser) {
        return storyReviewRepository.getStoryReviewsWrittenBy(requestedUser.getUuid().toString());
    }

    public List<StoryReviewEntity> getReviewsOf(UUID storyUuid) {
        return storyReviewRepository.getStoryReviewsOf(storyUuid.toString());
    }

    public Optional<StoryReviewEntity> getReview(UUID reviewUuid) {
        return storyReviewRepository.getReviewByUuid(reviewUuid.toString());
    }

    public StoryReviewEntity updateReview(UUID reviewUuid, StoryReviewCreateRequestDto requestDto, AuthenticatedUser requestedUser) {
        StoryReviewEntity entity = getReview(reviewUuid)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 소유자가 아니면 수정 불가
        if (!entity.getUserUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        storyReviewRepository.update(reviewUuid.toString(), requestDto);

        return storyReviewRepository.getReviewByUuid(reviewUuid.toString())
                .orElseThrow(() -> new RuntimeException("Failed to update review."));
    }

    public void deleteReview(UUID reviewUuid, AuthenticatedUser requestedUser) {
        StoryReviewEntity entity = getReview(reviewUuid)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 소유자가 아니면 삭제 불가
        if (!entity.getUserUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        storyReviewRepository.delete(reviewUuid.toString());
    }
}
