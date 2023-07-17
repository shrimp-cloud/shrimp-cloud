package com.wkclz.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FileS3Config {

    /**
     * S3
     */

    @Value("${shrimp.cloud.file.s3.bucket:}")
    private String bucket;
    @Value("${shrimp.cloud.file.s3.region:}")
    private String region;
    @Value("${shrimp.cloud.file.s3.endpoint:}")
    private String endpoint;
    @Value("${shrimp.cloud.file.s3.bucket-domain:}")
    private String bucketDomain;
    @Value("${shrimp.cloud.file.s3.access-key-id:}")
    private String accessKeyId;
    @Value("${shrimp.cloud.file.s3.secret-key-secret:}")
    private String secretKeySecret;

}