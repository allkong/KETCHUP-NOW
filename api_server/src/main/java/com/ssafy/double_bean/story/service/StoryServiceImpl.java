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
        String imagePath = String.format("%s/story-images", author.getUuid());

        URI imageUri = s3Client.uploadFile(imagePath, imageFile);
        System.out.println(imageUri);
        StoryEntity requestEntity = createDto.toRequestEntity(imageUri);
        storyRepository.createFirstStory(userEntity.getId(), requestEntity);

        System.out.println(">>> " + requestEntity.getId() + " / ");

        // 전달 받은 id를 토대로 생성된 스토리 정보를 반환
        return storyRepository.findById(requestEntity.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create first story."));
    }
}
