package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.aws.s3.S3Service;
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

import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SpotService {
    private final SpotRepository spotRepository;
    private final StoryRepository storyRepository;
    private final S3Service s3Service;

    public SpotService(SpotRepository spotRepository, StoryRepository storyRepository, S3Service s3Service) {
        this.spotRepository = spotRepository;
        this.storyRepository = storyRepository;
        this.s3Service = s3Service;
    }

    public List<SpotEntity> getSpotsOf(UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity targetStory = storyRepository.getStoryByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        if (!targetStory.getAuthorUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }

        List<SpotEntity> spots = spotRepository.getSpotsOf(storyUuid.toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 이미지 서명 및 반환
        return spots.stream().map(this::setPresignedUriFields).toList();
    }

    private double getSpotOrderIndex(List<SpotEntity> spots, UUID prviousSpotUuid) {
        // 첫 스팟인 경우
        if (spots.isEmpty()) {
            // 어떤 스팟의 뒤에 삽입할 수 없음
            if (prviousSpotUuid != null) {
                throw new HttpResponseException(ErrorCode.SPOT_MODIFY_FAILED);
            }

            // 처음 삽입되는 인덱스는 0으로 항상 고정
            return 0.0;
        }
        // 첫 스팟이 아닌 경우
        else {
            // prviousSpotUuid가 전달되지 않았다면, 맨 앞에 삽입하는 것을 의미함
            if (prviousSpotUuid == null) {
                // 따라서, 첫 스팟의 인덱스에서 일정 값을 빼준 값으로 인덱스 설정
                return spots.get(0).getOrderIndex() - SpotRepository.ORDER_INDEX_GAP;
            }
            // 맨 뒤에 삽입하는 경우
            // 마지막 스팟의 인덱스에서 일정 값을 빼준 값으로 인덱스 설정
            else if (spots.get(spots.size() - 1).getUuid().equals(prviousSpotUuid)) {
                return spots.get(spots.size() - 1).getOrderIndex() + SpotRepository.ORDER_INDEX_GAP;
            }
            // 가운데에 삽입하는 경우
            else {
                // 특정한 스팟이 없으면 에러
                int frontTargetIdx = 0;
                while (frontTargetIdx < spots.size()) {
                    if (spots.get(frontTargetIdx).getUuid().equals(prviousSpotUuid)) {
                        break;
                    } else {
                        frontTargetIdx++;
                    }
                }

                if (frontTargetIdx == spots.size()) {
                    throw new HttpResponseException(ErrorCode.NOT_FOUND);
                }

                // 있다면, 두 스팟의 인덱스 평균값을 인덱스로 설정한다.
                SpotEntity previousSpot = spots.get(frontTargetIdx);
                SpotEntity nextSpot = spots.get(frontTargetIdx + 1);
                return (previousSpot.getOrderIndex() + nextSpot.getOrderIndex()) / 2;
            }
        }
    }

    @Transactional
    public SpotEntity insertSpotTo(UUID storyUuid, SpotInsertRequestDto requestDto, MultipartFile imageFile, AuthenticatedUser requestedUser) throws IOException {
        StoryEntity targetStory = storyRepository.getStoryByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 소유자가 아닌 경우 갱신 거절
        if (!targetStory.getAuthorUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }
        // 이미 배포된 경우 갱신 거절
        else if (targetStory.getStatus() != StoryEntity.StoryStatus.WRITING) {
            throw new HttpResponseException(ErrorCode.ALREADY_PUBLISHED_STORY);
        }

        // order index 오름차순 정렬
        List<SpotEntity> spots = spotRepository.getSpotsOf(storyUuid.toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 삽입 위치를 표현하기 위한 인덱스 받아서 설정
        SpotEntity requestEntity = requestDto.toRequestEntity();
        // 인덱스 키 설정
        requestEntity.setOrderIndex(getSpotOrderIndex(spots, requestDto.previousSpotUuid()));

        // 이미지가 있다면 이미지 업로드
        if (imageFile != null) {
            String[] objectKeys = s3Service.getImageObjectKeys(requestedUser, imageFile);
            String originalKey = objectKeys[0];
            String thumbnailKey = objectKeys[1];

            s3Service.uploadFile(originalKey, imageFile);
            URI originalUri = s3Service.getUri(originalKey);
            URI thumbnailUri = s3Service.getUri(thumbnailKey);

            requestEntity.setImageUri(originalUri);
            requestEntity.setThumbnailImageUri(thumbnailUri);
        }

        // 최종 삽입 요청
        spotRepository.insertNewSpot(storyUuid.toString(), requestEntity);
        int insertedSpotId = requestEntity.getId();
        SpotEntity insertedEntity = spotRepository.findSpotById(insertedSpotId)
                .orElseThrow(() -> new RuntimeException("Failed to insert spot to story."));

        // 이미지 서명 및 반환
        setPresignedUriFields(insertedEntity);
        return insertedEntity;
    }

    private SpotEntity setPresignedUriFields(SpotEntity entity) {
        if (entity.getImageUri() != null) {
            entity.setImageUri(s3Service.getPresignedUri(entity.getImageUri()));
        }
        if (entity.getThumbnailImageUri() != null) {
            entity.setThumbnailImageUri(s3Service.getPresignedUri(entity.getThumbnailImageUri()));
        }
        if (entity.getEventImageUri() != null) {
            entity.setEventImageUri(s3Service.getPresignedUri(entity.getEventImageUri()));
        }
        if (entity.getEventThumbnailImageUri() != null) {
            entity.setEventThumbnailImageUri(s3Service.getPresignedUri(entity.getEventThumbnailImageUri()));
        }
        return entity;
    }
}
