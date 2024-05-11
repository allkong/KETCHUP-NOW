package com.ssafy.double_bean.story.service;

import com.ssafy.double_bean.aws.s3.S3Client;
import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.story.dto.StoryCreateRequestDto;
import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.repository.StoryRepository;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;


    public StoryServiceImpl(StoryRepository storyRepository, UserRepository userRepository, S3Client s3Client) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
    }

    @Override
    public StoryEntity createFirstStory(AuthenticatedUser author, StoryCreateRequestDto createDto, MultipartFile imageFile) throws IOException, URISyntaxException {
        // 요청한 사용자를 작성자로 새 스토리 및 스토리 베이스 생성
        UserEntity userEntity = userRepository.findByUuid(author.getUuid())
                .orElseThrow(() -> new HttpResponseException(ErrorCode.UNKNOWN_USER));

        // 이미지 업로드
        String[] objectKeys = getStoryImageObjectKeys(author, imageFile);
        String originalKey = objectKeys[0];
        String thumbnailKey = objectKeys[1];

        s3Client.uploadFile(originalKey, imageFile);
        URI originalUri = s3Client.getUri(originalKey);
        URI thumbnailUri = s3Client.getUri(thumbnailKey);

        StoryEntity requestEntity = createDto.toRequestEntity(originalUri, thumbnailUri);
        storyRepository.createFirstStory(userEntity.getId(), requestEntity);

        // 전달 받은 id를 토대로 생성된 스토리 정보를 반환 (story base id가 반환됨 ㅠㅠ)
        StoryEntity createdStory = storyRepository.findWritingStoryByStoryBaseId(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create first story."));

        if (createdStory.getImageUri() != null) {
            createdStory.setImageUri(s3Client.getPresignedUri(createdStory.getImageUri()));
        }
        if (createdStory.getThumbnailImageUri() != null) {
            createdStory.setThumbnailImageUri(s3Client.getPresignedUri(createdStory.getThumbnailImageUri()));
        }

        return createdStory;
    }

    private String[] getStoryImageObjectKeys(AuthenticatedUser author, MultipartFile imageFile) {
        UUID fileUuid = UUID.randomUUID();
        Long timestamp = System.currentTimeMillis();
        String original = String.format("images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, imageFile.getOriginalFilename());
        String thumbnail = String.format("thumbnail-images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, imageFile.getOriginalFilename());
        return new String[]{original, thumbnail};
    }
}
