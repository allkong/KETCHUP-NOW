package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.SpotInsertRequestDto;
import com.ssafy.double_bean.story.model.entity.SpotEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.SpotRepository;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SpotService {
    private final SpotRepository spotRepository;
    private final StoryRepository storyRepository;

    public SpotService(SpotRepository spotRepository, StoryRepository storyRepository) {
        this.spotRepository = spotRepository;
        this.storyRepository = storyRepository;
    }

    public List<SpotEntity> getSpotsOf(UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity targetStory = storyRepository.getStoryByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        if (targetStory.getAuthorUuid() != requestedUser.getUuid()) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }

        return spotRepository.getAll();
    }

    @Transactional
    public SpotEntity insertSpotTo(UUID storyUuid, SpotInsertRequestDto requestDto, MultipartFile imageFile, AuthenticatedUser requestedUser) {
        StoryEntity targetStory = storyRepository.getStoryByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        if (targetStory.getAuthorUuid() != requestedUser.getUuid()) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }

        // order index 오름차순 정렬
        List<SpotEntity> spots = spotRepository.getSpotsOf(storyUuid.toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));
        SpotEntity requestEntity = requestDto.toRequestEntity();

        // 첫 스팟인 경우
        if (spots.isEmpty()) {
            // 어떤 스팟의 뒤에 삽입할 수 없음
            if (requestDto.frontSpotUuid() != null) {
                throw new HttpResponseException(ErrorCode.SPOT_MODIFY_FAILED);
            }

            // 처음 삽입되는 인덱스는 0으로 항상 고정
            requestEntity.setOrderIndex(0.0);
        }
        // 첫 스팟이 아닌 경우
        else {
            // frontSpotUuid가 전달되지 않았다면, 맨 앞에 삽입하는 것을 의미함
            if (requestDto.frontSpotUuid() == null) {
                // 따라서, 첫 스팟의 인덱스에서 일정 값을 빼준 값으로 인덱스 설정
                requestEntity.setOrderIndex(spots.get(0).getOrderIndex() - SpotRepository.ORDER_INDEX_GAP);
            }
            // 맨 뒤에 삽입하는 경우
            // 마지막 스팟의 인덱스에서 일정 값을 빼준 값으로 인덱스 설정
            else if (spots.get(spots.size() - 1).getUuid().equals(requestDto.frontSpotUuid())) {
                requestEntity.setOrderIndex(spots.get(spots.size() - 1).getOrderIndex() + SpotRepository.ORDER_INDEX_GAP);
            }
            // 가운데에 삽입하는 경우
            else {
                // 특정한 스팟이 없으면 에러
                int frontTargetIdx = 0;
                while (frontTargetIdx < spots.size()) {
                    if (spots.get(frontTargetIdx).getUuid().equals(requestDto.frontSpotUuid())) {
                        break;
                    } else {
                        frontTargetIdx++;
                    }
                }

                if (frontTargetIdx == spots.size()) {
                    throw new HttpResponseException(ErrorCode.NOT_FOUND);
                }

                // 있다면, 두 스팟의 인덱스 평균값을 인덱스로 설정한다.
                SpotEntity frontSpot = spots.get(frontTargetIdx);
            }
        }

        return null;
    }
}
