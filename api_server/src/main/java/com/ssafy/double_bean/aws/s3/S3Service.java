package com.ssafy.double_bean.aws.s3;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.double_bean.user.dto.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static com.ssafy.double_bean.common.constant.TimeUnit.HOURS;

@Service
public class S3Service {
    private final String BUCKET_NAME;
    private final String REGION;
    private final AmazonS3Client s3Client;

    public S3Service(@Value("${aws.s3.bucket-name}") String bucketName, @Value("${aws.region}") String region, AmazonS3Client s3Client) {
        this.BUCKET_NAME = bucketName;
        this.REGION = region;
        this.s3Client = s3Client;
    }

    public void uploadFile(String objectKey, MultipartFile file) throws IOException {
        // 메타 정보 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // 업로드
        PutObjectRequest uploadRequest = new PutObjectRequest(BUCKET_NAME, objectKey, file.getInputStream(), metadata);
        s3Client.putObject(uploadRequest);
    }

    public URI getUri(String objectKey) {
        String uriString = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, objectKey);
        return URI.create(uriString);
    }

    public URI getPresignedUri(URI originalUri) {
        try {
            String bucketName = originalUri.getHost().split("\\.")[0];
            String objectKey = originalUri.getPath().substring(1);

            Date expiration = new Date(System.currentTimeMillis() + HOURS);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            return s3Client.generatePresignedUrl(request).toURI();
        } catch (Exception e) {
            return null;
        }
    }

    public void removeItem(URI targetUri) {
        if (targetUri == null || targetUri.toString().isBlank()) {
            return;
        }

        String bucketName = targetUri.getHost().split("\\.")[0];
        String objectKey = targetUri.getPath().substring(1);

        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objectKey);
        s3Client.deleteObject(request);
    }

    public String[] getImageObjectKeys(AuthenticatedUser author, MultipartFile imageFile) {
        return getImageObjectKeys(author, imageFile.getOriginalFilename());
    }

    public String[] getImageObjectKeys(AuthenticatedUser author, String originalFilename) {
        UUID fileUuid = UUID.randomUUID();
        Long timestamp = System.currentTimeMillis();
        String original = String.format("images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, originalFilename);
        String thumbnail = String.format("thumbnail-images/%s/%s_%s_%s", author.getUuid(), fileUuid, timestamp, originalFilename);
        return new String[]{original, thumbnail};
    }

    public void duplicateFileIfExists(URI sourceUri, String destObjectKey) {
        if (sourceUri == null || sourceUri.toString().isBlank()) {
            return;
        }

        String bucketName = sourceUri.getHost().split("\\.")[0];
        String sourceObjectKey = sourceUri.getPath().substring(1);

        s3Client.copyObject(bucketName, sourceObjectKey, bucketName, destObjectKey);
    }
}
