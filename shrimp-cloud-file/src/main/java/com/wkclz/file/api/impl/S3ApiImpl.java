package com.wkclz.file.api.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.wkclz.file.api.FileApi;
import com.wkclz.file.api.S3Api;
import com.wkclz.file.config.FileS3Config;
import com.wkclz.file.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("S3Api")
public class S3ApiImpl implements FileApi, S3Api {

    @Autowired
    private FileS3Config config;

    @Override
    public String upload(MultipartFile file, String businessType) {

        String region = config.getRegion();
        String bucket = config.getBucket();
        String endpoint = config.getEndpoint();
        String bucketDomain = config.getBucketDomain();
        String accessKeyId = config.getAccessKeyId();
        String secretKeySecret = config.getSecretKeySecret();

        String filename = file.getOriginalFilename();
        String key = OssUtil.getFullName(businessType, filename);

        // 通过 accessKeyId、secretKey 生成认证的aws凭证对象
        // 通过凭证对象 awsCredentials 跟 区域 region 生成s3的服务端(ap-east-1亚太地区香港的区域)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(getEndpointConfiguration(endpoint, region))
            .withCredentials(getCredentialsProvider(accessKeyId, secretKeySecret))
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

        // 自定义了 bucket 域名场景
        if (StringUtils.isNotBlank(bucketDomain)) {
            if (!bucketDomain.endsWith("/")) {
                bucketDomain = bucketDomain + "/";
            }
            return bucketDomain + key;
        }

        URL url = s3.getUrl(bucket, key);
        return url.toString();
    }


    /**
     * S3 单文件删除
     */
    public Integer delete(String objectName) {
        List<String> objectNames = new ArrayList<>();
        objectNames.add(objectName);
        return delete(objectNames);
    }
    /**
     * 多文件删除
     */
    public Integer delete(List<String> objectNames) {

        String endpoint = config.getEndpoint();
        String accessKeyId = config.getAccessKeyId();
        String secretKeySecret = config.getSecretKeySecret();
        String region = config.getRegion();
        String bucket = config.getBucket();

        // 通过 accessKeyId、secretKey 生成认证的aws凭证对象
        // 通过凭证对象 awsCredentials 跟 区域 region 生成s3的服务端(ap-east-1亚太地区香港的区域)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(getEndpointConfiguration(endpoint, region))
            .withCredentials(getCredentialsProvider(accessKeyId, secretKeySecret))
            .build();

        List<DeleteObjectsRequest.KeyVersion> keyVersions = objectNames.stream().map(DeleteObjectsRequest.KeyVersion::new).collect(Collectors.toList());
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket);
        request.setKeys(keyVersions);
        DeleteObjectsResult deleteObjectsResult = s3.deleteObjects(request);
        List<DeleteObjectsResult.DeletedObject> deletedObjects = deleteObjectsResult.getDeletedObjects();
        return deletedObjects.size();
    }

    /**
     * 生成预签名对象 URL
     */
    public String getSignatureUrl(String key) {
        String endpoint = config.getEndpoint();
        String accessKeyId = config.getAccessKeyId();
        String secretKeySecret = config.getSecretKeySecret();
        String region = config.getRegion();
        String bucket = config.getBucket();

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(getEndpointConfiguration(endpoint, region))
            .withCredentials(getCredentialsProvider(accessKeyId, secretKeySecret))
            .build();

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        long timeOut = 7 * 24 * 60 * 60 * 1000L;
        expTimeMillis += timeOut;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
        URL fileUrl = s3.generatePresignedUrl(request);
        String url = fileUrl.toString();
        System.out.println("Pre-Signed URL: " + url);
        return url;
    }

    private static AWSStaticCredentialsProvider getCredentialsProvider(String accessKeyId, String secretKey) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        return new AWSStaticCredentialsProvider(credentials);
    }

    private static AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String endpoint, String region) {
        return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
    }

}
