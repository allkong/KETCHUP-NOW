package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.aws.s3.S3Service;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Client;


    public StoryServiceImpl(StoryRepository storyRepository, UserRepository userRepository, S3Service s3Client) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
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
            String[] objectKeys = getStoryImageObjectKeys(author, imageFile);
            String originalKey = objectKeys[0];
            String thumbnailKey = objectKeys[1];

            s3Client.uploadFile(originalKey, imageFile);
            URI originalUri = s3Client.getUri(originalKey);
            URI thumbnailUri = s3Client.getUri(thumbnailKey);
            requestEntity = createDto.toRequestEntity(originalUri, thumbnailUri);
        }
        storyRepository.createFirstStory(userEntity.getId(), requestEntity);

        // 전달 받은 id를 토대로 생성된 스토리 정보를 반환 (story base id가 반환됨 ㅠㅠ)
        StoryEntity createdStory = storyRepository.findWritingStoryByStoryBaseId(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create first story."));

        presignUriFields(createdStory);

        return createdStory;
    }

    private String[] getStoryImageObjectKeys(AuthenticatedUser author, MultipartFile imageFile) {
        UUID fileUuid = UUID.randomUUID();
        Long timestamp = System.currentTimeMillis();
        String original = String.format("images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, imageFile.getOriginalFilename());
        String thumbnail = String.format("thumbnail-images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, imageFile.getOriginalFilename());
        return new String[]{original, thumbnail};
    }

    @Override
    public List<StoryEntity> getStoryBaseAndLatestStory(AuthenticatedUser requestedUser) {
        List<StoryEntity> entities = storyRepository.getLatestStoriesOf(requestedUser.getUuid().toString());
        return entities.stream().map(this::presignUriFields).toList();
    }

    private StoryEntity presignUriFields(StoryEntity entity) {
        if (entity.getImageUri() != null) {
            entity.setImageUri(s3Client.getPresignedUri(entity.getImageUri()));
        }
        if (entity.getThumbnailImageUri() != null) {
            entity.setThumbnailImageUri(s3Client.getPresignedUri(entity.getThumbnailImageUri()));
        }
        return entity;
    }

    @Override
    public List<StoryEntity> getStoriesOf(UUID storyBaseUuid, AuthenticatedUser requestedUser) {
        return storyRepository.getSubStoriesOf(storyBaseUuid.toString(), requestedUser.getUuid().toString());
    }

    @Override
    public StoryEntity getStory(UUID storyBaseUuid, UUID storyUuid, AuthenticatedUser requestedUser) {
        StoryEntity story = storyRepository.findByUuid(storyUuid.toString())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.NOT_FOUND));
        System.out.println(story);

        // 작성자가 아닌데 작성중인 스토리에 접근하는 경우
        if (!story.getAuthorUuid().equals(requestedUser.getUuid()) && story.getStatus() == StoryStatus.WRITING) {
            throw new HttpResponseException(ErrorCode.HAS_NO_OWNERSHIP);
        }


        // Story base id가 유효하지 않은 경우
        else if (!story.getStoryBaseUuid().equals(storyBaseUuid)) {
            throw new HttpResponseException(ErrorCode.NOT_FOUND);
        }

        return story;
    }
}
