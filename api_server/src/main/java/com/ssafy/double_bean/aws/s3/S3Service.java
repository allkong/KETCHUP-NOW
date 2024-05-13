package com.ssafy.double_bean.aws.s3;


import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

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
}
