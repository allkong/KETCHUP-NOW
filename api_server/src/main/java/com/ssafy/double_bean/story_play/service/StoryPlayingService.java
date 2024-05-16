package com.ssafy.double_bean.story_play.service;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import com.ssafy.double_bean.story_play.model.repository.StoryPlayingRepository;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoryPlayingService {
    private final StoryPlayingRepository storyPlayingRepository;
    private final StoryRepository storyRepository;

    public StoryPlayingService(StoryPlayingRepository storyPlayingRepository, StoryRepository storyRepository) {
        this.storyPlayingRepository = storyPlayingRepository;
        this.storyRepository = storyRepository;
    }

    public StoryPlayingEntity startPlaying(UUID storyUuid, AuthenticatedUser requestedUser) {
        // 이미 플레이 중인 스토리가 있는 경우 플레이 불가
        if (storyPlayingRepository.hasPlayingGame(requestedUser.getUuid().toString())) {
            throw new HttpResponseException(ErrorCode.USER_ALREADY_PLAYING_GAME);
        }

        StoryEntity story = storyRepository.findByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // PUBLISHED 상태인 스토리만 플레이 가능
        if (story.getStatus() != StoryEntity.StoryStatus.PUBLISHED) {
            throw new HttpResponseException(ErrorCode.CANNOT_PLAY_WRITING_STORY);
        }

        // 플레이 중인 게임이 없으면 게임 기록 생성
        StoryPlayingEntity requestEntity = StoryPlayingEntity
                .getStartRequestEntity(story.getId(), requestedUser.getUuid());
        storyPlayingRepository.createGamePlaying(requestEntity);

        return this.findById(requestEntity.getId());
    }

    public StoryPlayingEntity findById(int id) {
        return storyPlayingRepository.findById(id)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));
    }

    public List<StoryPlayingEntity> getStoryPlayingsOf(int storyId) {
        return storyPlayingRepository.getByStoryId(storyId);
    }

    public List<StoryPlayingEntity> getStoryPlayingsOf(AuthenticatedUser requestedUser) {
        return storyPlayingRepository.getByPlayerUuid(requestedUser.getUuid().toString());
    }

    public Optional<StoryPlayingEntity> getPlayingOne(AuthenticatedUser requestedUser) {
        return storyPlayingRepository.getCurrentPlaying(requestedUser.getUuid().toString());
    }

    public void deleteCurrentPlay(AuthenticatedUser requestedUser) {
        StoryPlayingEntity entity = getPlayingOne(requestedUser)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));
        storyPlayingRepository.deleteCurrentPlaying(requestedUser.getUuid().toString());
    }
}
