package com.madfood.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3PresignService {

    private static final Logger logger = LoggerFactory.getLogger(S3PresignService.class);

    private final S3Presigner presigner;
    private final S3Client s3Client;
    private final String bucket;
    private final String prefix;

    public S3PresignService(@Value("${AWS_REGION:us-east-1}") String region,
                            @Value("${S3_BUCKET:}") String bucket,
                            @Value("${S3_PREFIX:}") String prefix) {
        this.presigner = S3Presigner.builder().region(Region.of(region)).build();
        this.s3Client = S3Client.builder().region(Region.of(region)).build();
        this.bucket = bucket;
        this.prefix = (prefix == null) ? "" : prefix;
        logger.info("S3PresignService configured for bucket='{}' prefix='{}' region='{}'", bucket, prefix, region);
    }

    public PresignResult presignUpload(String filename, String contentType, int validMinutes) {
        String key = buildKey(filename);

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .acl("private")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(por)
                .signatureDuration(Duration.ofMinutes(validMinutes))
                .build();

        URL url = presigner.presignPutObject(presignRequest).url();
        return new PresignResult(key, url.toString(), validMinutes);
    }

    public PresignResult presignDownload(String key, int validMinutes) {
        GetObjectRequest gor = GetObjectRequest.builder().bucket(bucket).key(key).build();
        GetObjectPresignRequest gpReq = GetObjectPresignRequest.builder()
                .getObjectRequest(gor)
                .signatureDuration(Duration.ofMinutes(validMinutes))
                .build();
        URL url = presigner.presignGetObject(gpReq).url();
        return new PresignResult(key, url.toString(), validMinutes);
    }

    private String buildKey(String filename) {
        String uuid = UUID.randomUUID().toString();
        String safe = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (prefix != null && !prefix.isBlank()) return prefix + "/" + uuid + "_" + safe;
        return uuid + "_" + safe;
    }

    public record PresignResult(String key, String url, int expiresMinutes) { }
}
