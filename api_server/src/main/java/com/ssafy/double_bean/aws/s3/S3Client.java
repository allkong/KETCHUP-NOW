package com.ssafy.double_bean.aws.s3;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import static com.ssafy.double_bean.common.constant.TimeUnit.HOURS;

@Component
public class S3Client {
    private final String BUCKET_NAME;
    private final String REGION;
    private final AmazonS3Client s3Client;

    public S3Client(@Value("${aws.s3.bucket-name}") String bucketName, @Value("${aws.region}") String region, AmazonS3Client s3Client) {
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
            e.printStackTrace();
            return null;
        }
    }
}