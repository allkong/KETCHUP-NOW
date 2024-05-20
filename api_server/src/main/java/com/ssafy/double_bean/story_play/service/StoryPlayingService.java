package com.ssafy.double_bean.story_play.service;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.model.entity.SpotEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.SpotRepository;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.story_play.dto.StoryPlayingCreateRequestDto;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;
import com.ssafy.double_bean.story_play.model.entity.StoryPlayingLogEntity;
import com.ssafy.double_bean.story_play.model.repository.StoryPlayingLogRepository;
import com.ssafy.double_bean.story_play.model.repository.StoryPlayingRepository;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoryPlayingService {
    private final StoryPlayingRepository storyPlayingRepository;
    private final StoryRepository storyRepository;
    private final StoryPlayingLogRepository storyPlayingLogRepository;
    private final SpotRepository spotRepository;

    public StoryPlayingService(StoryPlayingRepository storyPlayingRepository, StoryRepository storyRepository,
                               StoryPlayingLogRepository storyPlayingLogRepository, SpotRepository spotRepository) {
        this.storyPlayingRepository = storyPlayingRepository;
        this.storyRepository = storyRepository;
        this.storyPlayingLogRepository = storyPlayingLogRepository;
        this.spotRepository = spotRepository;
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

    public List<StoryPlayingLogEntity> getCurrentStoryPlayingLogs(AuthenticatedUser requestedUser) {
        StoryPlayingEntity currentPlaying = getPlayingOne(requestedUser)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_PLAYING_GAME));
        return storyPlayingLogRepository.getStoryPlayingLogsUnder(currentPlaying.getId());
    }

    public StoryPlayingLogEntity createStoryPlayingLog(StoryPlayingCreateRequestDto dto, AuthenticatedUser requestedUser) {
        StoryPlayingEntity currentPlaying = getPlayingOne(requestedUser)
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_PLAYING_GAME));

        // 이미 완료한 플레이면 클리어가 불가능함
        if (currentPlaying.getClearedAt() != null) {
            throw new HttpResponseException(ErrorCode.ALREADY_CLEARED_PLAYING);
        }

        // 스팟 배치 순서대로 정렬
        List<SpotEntity> spots = spotRepository.getSpotsOf(currentPlaying.getStoryUuid().toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 지금 완료하려는 스팟의 uuid
        UUID targetSpotUuid = dto.spotUuid();
        // 요청한 스팟을 해당 스토리가 포함하고 있는지 확인
        boolean containsRequestedSpot = spots.stream().anyMatch(spot -> spot.getUuid().equals(targetSpotUuid));
        if (!containsRequestedSpot) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        // 이때까지 완료한 로그 목록 조회
        List<StoryPlayingLogEntity> logs = storyPlayingLogRepository.getStoryPlayingLogsUnder(currentPlaying.getId());

        // 이미 완료한 스팟은 클리어할 수 없음
        boolean alreadyClearedSpot = logs.stream().anyMatch(log -> log.getSpotUuid().equals(targetSpotUuid));
        if (alreadyClearedSpot) {
            throw new HttpResponseException(ErrorCode.ALREADY_CLEARED_SPOT);
        }


        // 클리어하지 않은 가장 앞의 스팟을 찾음
        SpotEntity unclearedSpot = null;

        // 스팟의 맨 앞부터 대조하며 선행 스팟을 클리어했는지 확인
        for (SpotEntity spot : spots) {
            boolean cleared = false;
            for (StoryPlayingLogEntity log : logs) {
                if (spot.getUuid().equals(log.getSpotUuid())) {
                    cleared = true;
                    break;
                }
            }

            if (!cleared) {
                unclearedSpot = spot;
                break;
            }
        }

        // 만약 모든 스팟을 클리어했다면, 이미 완료된 플레이
        if (unclearedSpot == null) {
            throw new HttpResponseException(ErrorCode.ALREADY_CLEARED_PLAYING);
        }

        // 클리어하지 않은 가장 첫 스팟만 클리어할 수 있음
        if (!unclearedSpot.getUuid().equals(targetSpotUuid)) {
            throw new HttpResponseException(ErrorCode.INVALID_SPOT_CLEAR_ORDER);
        }

        // 스팟 클리어 기록 생성
        StoryPlayingLogEntity requestEntity = StoryPlayingLogEntity.getCreateRequestEntity(
                requestedUser.getUuid(), currentPlaying.getUuid(), targetSpotUuid);
        storyPlayingLogRepository.insert(requestEntity);

        StoryPlayingLogEntity createdEntity = storyPlayingLogRepository.getById(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to created spot clear log."));

        // 방금 클리어한 스팟이 마지막 스팟이면 스토리 클리어 처리
        if (createdEntity.getSpotUuid().equals(spots.get(spots.size() - 1).getUuid())) {
            storyPlayingRepository.setClearStoryPlaying(createdEntity.getStoryPlayingUuid().toString());
        }

        // 생성된 기록 반환
        return createdEntity;
    }

    public List<StoryPlayingLogEntity> getLogsOfPlaying(UUID playingUuid, AuthenticatedUser requestedUser) {
        StoryPlayingEntity playing = storyPlayingRepository.getByUuid(playingUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        if (!playing.getPlayerUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        return storyPlayingLogRepository.getStoryPlayingLogsUnder(playing.getId());
    }
}
