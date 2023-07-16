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


    @Value("${shrimp.cloud.file.alioss.inner-endpoint:}")
    private String innerEndpoint;
    @Value("${shrimp.cloud.file.alioss.outer-endpoint:}")
    private String outerEndpoint;
    @Value("${shrimp.cloud.file.alioss.access-key-id:}")
    private String accessKeyId;
    @Value("${shrimp.cloud.file.alioss.secret-key-secret:}")
    private String secretKeySecret;
    @Value("${shrimp.cloud.file.alioss.bucket:}")
    private String bucket;


}