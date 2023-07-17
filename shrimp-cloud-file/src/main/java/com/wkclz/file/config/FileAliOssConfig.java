package com.wkclz.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FileAliOssConfig {

    /**
     * AliOss
     */


    @Value("${shrimp.cloud.file.alioss.bucket:}")
    private String bucket;
    @Value("${shrimp.cloud.file.alioss.endpoint:}")
    private String endpoint;
    @Value("${shrimp.cloud.file.alioss.bucket-domain:}")
    private String bucketDomain;
    @Value("${shrimp.cloud.file.alioss.access-key-id:}")
    private String accessKeyId;
    @Value("${shrimp.cloud.file.alioss.secret-key-secret:}")
    private String secretKeySecret;


}