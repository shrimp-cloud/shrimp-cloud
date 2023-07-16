package com.wkclz.file.api.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wkclz.file.api.FileApi;
import com.wkclz.file.api.S3Api;
import com.wkclz.file.config.FileConfig;
import com.wkclz.file.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Service("S3Api")
public class S3ApiImpl implements FileApi, S3Api {

    @Autowired
    private FileConfig config;

    @Override
    public String upload(MultipartFile file, String businessType) {

        String endpoint = config.getS3Endpoint();
        String accessKeyId = config.getS3AccessKeyId();
        String secretKey = config.getS3SecretKey();
        String region = config.getS3Region();
        String bucket = config.getS3Bucket();

        String filename = file.getOriginalFilename();
        String key = OssUtil.getFullName(businessType, filename);

        // 通过 accessKeyId、secretKey 生成认证的aws凭证对象
        // 通过凭证对象 awsCredentials 跟 区域 region 生成s3的服务端(ap-east-1亚太地区香港的区域)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(getEndpointConfiguration(endpoint, region))
            .withCredentials(getCredentialsProvider(accessKeyId, secretKey))
            .build();

        // 设置文件元数据
        // ContentType: 默认不设置的话是什么ostream什么之类的访问链接的话就会下载文件
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(OssUtil.getContentType(filename));
        metadata.setContentLength(file.getSize());
        try {
            // 想要通过公网访问到图片,除了将桶的权限更改之外，还需要设置withCannedAcl(CannedAccessControlList.PublicRead)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file.getInputStream(), metadata);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(String.format("Upload file [%s] to AWS S3 failed! Error: %s", key, e.getMessage()));
        } finally {
            s3.shutdown();
        }
        URL url = s3.getUrl(bucket, key);
        return url.toString();
    }


    private static AWSStaticCredentialsProvider getCredentialsProvider(String accessKeyId, String secretKey) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        return new AWSStaticCredentialsProvider(credentials);
    }

    private static AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String endpoint, String region) {
        return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
    }

}
