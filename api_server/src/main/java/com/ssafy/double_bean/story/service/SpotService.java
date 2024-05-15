package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.aws.s3.S3Service;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.SpotInsertRequestDto;
import com.ssafy.double_bean.story.dto.SpotUpdateRequestDto;
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

    private StoryEntity getStoryWithOwnershipCheck(UUID storyUuid, AuthenticatedUser requestedUser, boolean forUpdate) {
        // 연결된 스토리를 찾아
        StoryEntity targetStory = storyRepository.getStoryByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 소유자가 아닌 경우 반환 거절
        if (!targetStory.getAuthorUuid().equals(requestedUser.getUuid())) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }

        // 이미 배포된 경우 반환 거절
        if (forUpdate && targetStory.getStatus() != StoryEntity.StoryStatus.WRITING) {
            throw new HttpResponseException(ErrorCode.ALREADY_PUBLISHED_STORY);
        }

        return targetStory;
    }

    private StoryEntity getStoryWithOwnershipCheck(UUID storyUuid, AuthenticatedUser requestedUser) {
        return getStoryWithOwnershipCheck(storyUuid, requestedUser, false);
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

    // Spot entity에 대해, presign이 필요한 모든 필드에 presign한 값을 넣어 준다.
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

    public List<SpotEntity> getSpotsOf(UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity targetStory = getStoryWithOwnershipCheck(storyUuid, requestedUser);

        List<SpotEntity> spots = spotRepository.getSpotsOf(targetStory.getUuid().toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 이미지 서명 및 반환
        return spots.stream().map(this::setPresignedUriFields).toList();
    }

    private void uploadAndSetImage(AuthenticatedUser requestedUser, SpotEntity requestEntity, MultipartFile imageFile) throws IOException {
        String[] objectKeys = s3Service.getImageObjectKeys(requestedUser, imageFile);
        String originalKey = objectKeys[0];
        String thumbnailKey = objectKeys[1];

        s3Service.uploadFile(originalKey, imageFile);
        URI originalUri = s3Service.getUri(originalKey);
        URI thumbnailUri = s3Service.getUri(thumbnailKey);

        requestEntity.setImageUri(originalUri);
        requestEntity.setThumbnailImageUri(thumbnailUri);
    }

    private void uploadAndSetEventImage(AuthenticatedUser requestedUser, SpotEntity requestEntity, MultipartFile eventImageFile) throws IOException {
        String[] objectKeys = s3Service.getImageObjectKeys(requestedUser, eventImageFile);
        String originalKey = objectKeys[0];
        String thumbnailKey = objectKeys[1];

        s3Service.uploadFile(originalKey, eventImageFile);
        URI originalUri = s3Service.getUri(originalKey);
        URI thumbnailUri = s3Service.getUri(thumbnailKey);

        requestEntity.setEventImageUri(originalUri);
        requestEntity.setEventThumbnailImageUri(thumbnailUri);
    }

    @Transactional
    public SpotEntity insertSpotTo(UUID storyUuid, SpotInsertRequestDto requestDto, MultipartFile imageFile, AuthenticatedUser requestedUser) throws IOException {
        StoryEntity targetStory = getStoryWithOwnershipCheck(storyUuid, requestedUser, true);

        // order index 오름차순 정렬
        List<SpotEntity> spots = spotRepository.getSpotsOf(targetStory.getUuid().toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 삽입 위치를 표현하기 위한 인덱스 받아서 설정
        SpotEntity requestEntity = requestDto.toRequestEntity();
        // 인덱스 키 설정
        requestEntity.setOrderIndex(getSpotOrderIndex(spots, requestDto.previousSpotUuid()));

        // 이미지가 있다면 이미지 업로드
        if (imageFile != null) {
            uploadAndSetImage(requestedUser, requestEntity, imageFile);
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


    public SpotEntity updateSpot(UUID storyUuid, UUID spotUuid, SpotUpdateRequestDto requestDto, MultipartFile spotImageFile, MultipartFile eventImageFile, AuthenticatedUser requestedUser) throws IOException {
        // 연결된 스토리를 찾아
        StoryEntity targetStory = getStoryWithOwnershipCheck(storyUuid, requestedUser, true);

        // order index 오름차순 정렬
        List<SpotEntity> spots = spotRepository.getSpotsOf(targetStory.getUuid().toString());
        spots.sort(Comparator.comparingDouble(SpotEntity::getOrderIndex));

        // 요청된 스토리 하위 스팟 중에서 주어진 uuid를 가지는 스팟을 찾고
        int targetIdx = 0;
        while (targetIdx < spots.size()) {
            if (spots.get(targetIdx).getUuid().equals(spotUuid)) {
                break;
            } else {
                targetIdx++;
            }
        }
        // 만약 요청된 스토리에 해당 스팟이 등록되어 있지 않다면 자원을 찾을 수 없음을 반환
        if (targetIdx == spots.size()) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        SpotEntity targetSpot = spots.get(targetIdx);
        SpotEntity requestEntity = requestDto.toRequestEntity();
        // 순서가 바뀌었다면 (업데이트 요청된 내 앞의 스팟이 다른 것으로 바뀌었다면)
        if (
            // 1. 원래는 맨 앞이었는데 내 앞에 다른 스팟이 생겼거나
                (targetIdx == 0 && requestDto.previousSpotUuid() != null)
                        // 2. 내 앞의 스팟이 바뀌었거나
                        || (targetIdx > 0 && !spots.get(targetIdx - 1).getUuid().equals(requestDto.previousSpotUuid()))) {
            // 새 위치 할당
            requestEntity.setOrderIndex(getSpotOrderIndex(spots, requestDto.previousSpotUuid()));
        }
        // 순서가 안바뀌었으면 그대로 유지
        else {
            requestEntity.setOrderIndex(targetSpot.getOrderIndex());
        }

        // 이미지 파일이 있는 경우 이미지 할당
        if (spotImageFile != null) {
            // 원래 이미지가 있었다면 삭제
            s3Service.removeItem(targetSpot.getImageUri());
            s3Service.removeItem(targetSpot.getThumbnailImageUri());
            uploadAndSetImage(requestedUser, requestEntity, spotImageFile);
        }

        // 이벤트 이미지 파일이 있는 경우 이미지 할당
        if (eventImageFile != null) {
            // 원래 이미지가 있었다면 삭제
            s3Service.removeItem(targetSpot.getEventImageUri());
            s3Service.removeItem(targetSpot.getEventThumbnailImageUri());
            uploadAndSetEventImage(requestedUser, requestEntity, eventImageFile);
        }

        // 갱신 요청 및 Presign
        spotRepository.updateSpot(targetSpot.getId(), requestEntity);
        SpotEntity updatedEntity = spotRepository.findSpotById(targetSpot.getId())
                .orElseThrow(() -> new RuntimeException("Failed to update spot."));
        setPresignedUriFields(updatedEntity);

        // 갱신된 객체를 반환
        return updatedEntity;
    }

    public void deleteSpot(UUID storyUuid, UUID spotUuid, AuthenticatedUser requestedUser) {
        // 스토리 및 스팟 검색
        StoryEntity story = getStoryWithOwnershipCheck(storyUuid, requestedUser, true);
        List<SpotEntity> spots = spotRepository.getSpotsOf(storyUuid.toString());
        SpotEntity targetSpot = spots.stream()
                .filter(e -> e.getUuid().equals(spotUuid))
                .findFirst().orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 이미지가 있었으면 삭제
        s3Service.removeItem(targetSpot.getImageUri());
        s3Service.removeItem(targetSpot.getThumbnailImageUri());
        s3Service.removeItem(targetSpot.getEventImageUri());
        s3Service.removeItem(targetSpot.getEventThumbnailImageUri());

        spotRepository.deleteSpot(targetSpot.getId());
    }
}
