package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.aws.s3.S3Service;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.dto.StoryUpdateRequestDto;
import com.ssafy.double_bean.story.model.entity.SpotEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.story_play.service.StoryPlayingService;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Service
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final SpotService spotService;
    private final StoryPlayingService storyPlayingService;
    private final S3Service s3Service;


    public StoryServiceImpl(StoryRepository storyRepository, UserRepository userRepository, SpotService spotService, StoryPlayingService storyPlayingService, S3Service s3Service) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.spotService = spotService;
        this.storyPlayingService = storyPlayingService;
        this.s3Service = s3Service;
    }

    @Override
    public StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException {
        // 요청한 사용자를 작성자로 새 스토리 및 스토리 베이스 생성
        UserEntity userEntity = userRepository.findByUuid(author.getUuid())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.UNKNOWN_USER));

        StoryEntity requestEntity;
        // 이미지 업로드
        if (imageFile == null) {
            requestEntity = createDto.toRequestEntity();
        } else {
            String[] objectKeys = s3Service.getImageObjectKeys(author, imageFile);
            String originalKey = objectKeys[0];
            String thumbnailKey = objectKeys[1];

            s3Service.uploadFile(originalKey, imageFile);
            URI originalUri = s3Service.getUri(originalKey);
            URI thumbnailUri = s3Service.getUri(thumbnailKey);
            requestEntity = createDto.toRequestEntity(originalUri, thumbnailUri);
        }
        storyRepository.createFirstStory(userEntity.getId(), requestEntity);

        // 전달 받은 id를 토대로 생성된 스토리 정보를 반환 (story base id가 반환됨 ㅠㅠ)
        StoryEntity createdStory = storyRepository.findWritingStoryByStoryBaseId(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create first story."));

        setPresignedUriFields(createdStory);

        return createdStory;
    }

    @Override
    public List<StoryEntity> getStoryBaseAndLatestStory(AuthenticatedUser requestedUser) {
        List<StoryEntity> entities = storyRepository.getLatestStoriesOf(requestedUser.getUuid().toString());
        return entities.stream().map(this::setPresignedUriFields).toList();
    }

    private StoryEntity setPresignedUriFields(StoryEntity entity) {
        if (entity.getImageUri() != null) {
            entity.setImageUri(s3Service.getPresignedUri(entity.getImageUri()));
        }
        if (entity.getThumbnailImageUri() != null) {
            entity.setThumbnailImageUri(s3Service.getPresignedUri(entity.getThumbnailImageUri()));
        }
        return entity;
    }

    @Override
    public List<StoryEntity> getStoriesOf(UUID storyBaseUuid, AuthenticatedUser requestedUser) {
        return storyRepository.getSubStoriesOf(storyBaseUuid.toString(), requestedUser.getUuid().toString());
    }

    @Override
    public StoryEntity getStory(UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity story = storyRepository.findByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));

        // 작성자가 아닌데 작성중인 스토리에 접근하는 경우
        if (!story.getAuthorUuid().equals(requestedUser.getUuid()) && story.getStatus() == StoryStatus.WRITING) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }

        return story;
    }

    @Override
    public StoryEntity updateStory(UUID storyUuid, AuthenticatedUser requestedUser,
                                   StoryUpdateRequestDto updateDto, MultipartFile newImage) throws IOException, URISyntaxException {
        StoryEntity targetStory = getStory(storyUuid, requestedUser);

        // 상태가 PUBLISHED이면 수정 불가
        if (targetStory.getStatus() == StoryStatus.PUBLISHED) {
            throw new HttpResponseException(ErrorCode.ALREADY_PUBLISHED_STORY);
        }

        // 수정 DTO 생성
        targetStory.setTitle(updateDto.title());
        targetStory.setDescription(updateDto.description());
        targetStory.setStatus(updateDto.status());
        targetStory.setSido(updateDto.sido());
        targetStory.setGungu(updateDto.gungu());

        // 이미지 수정이 요청되었다면
        if (newImage != null) {
            // 이미 있었던 경우 기존 이미지 삭제해주고
            s3Service.removeItem(targetStory.getImageUri());
            s3Service.removeItem(targetStory.getThumbnailImageUri());

            // 새 이미지 할당
            String[] objectKeys = s3Service.getImageObjectKeys(requestedUser, newImage);
            String originalKey = objectKeys[0];
            String thumbnailKey = objectKeys[1];

            s3Service.uploadFile(originalKey, newImage);
            URI originalUri = s3Service.getUri(originalKey);
            URI thumbnailUri = s3Service.getUri(thumbnailKey);

            targetStory.setImageUri(originalUri);
            targetStory.setThumbnailImageUri(thumbnailUri);
        }

        // 최종 수정 요청
        storyRepository.updateStory(storyUuid.toString(), targetStory);

        // 수정된 객체에 presign해서 응답
        setPresignedUriFields(targetStory);

        return targetStory;
    }

    @Override
    public void deleteStory(UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity targetStory = getStory(storyUuid, requestedUser);

        // 해당 스토리를 플레이했거나 플레이하고 있는 기록이 있으면 삭제 불가
        if (!storyPlayingService.getStoryPlayingsOf(targetStory.getId()).isEmpty()) {
            throw new HttpResponseException(ErrorCode.CANNOT_DELETE_STORY_WITH_PLAYER);
        }

        // 이외의 경우, 스토리 삭제 가능
        List<SpotEntity> spots = spotService.getSpotsOf(targetStory.getUuid(), requestedUser);
        for (SpotEntity spot : spots) {
            // 일단 스토리에 연결된 스팟의 이미지 파일을 모두 삭제
            s3Service.removeItem(spot.getImageUri());
            s3Service.removeItem(spot.getThumbnailImageUri());
            s3Service.removeItem(spot.getEventImageUri());
            s3Service.removeItem(spot.getEventThumbnailImageUri());
        }

        // 스토리 이미지 삭제
        s3Service.removeItem(targetStory.getImageUri());
        s3Service.removeItem(targetStory.getThumbnailImageUri());

        // 스토리 삭제(스팟은 CASCADE로 연쇄 삭제됨)
        storyRepository.deleteById(targetStory.getId());
    }
}
