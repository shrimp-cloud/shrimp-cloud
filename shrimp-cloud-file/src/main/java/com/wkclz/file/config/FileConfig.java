package com.wkclz.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FileConfig {

    /**
     * S3
     */

    @Value("${shrimp.cloud.file.s3.endpoint:}")
    private String s3Endpoint;
    @Value("${shrimp.cloud.file.s3.access-key-id:}")
    private String s3AccessKeyId;
    @Value("${shrimp.cloud.file.s3.secret-key:}")
    private String s3SecretKey;
    @Value("${shrimp.cloud.file.s3.bucket:}")
    private String s3Bucket;
    @Value("${shrimp.cloud.file.s3.region:}")
    private String s3Region;

}