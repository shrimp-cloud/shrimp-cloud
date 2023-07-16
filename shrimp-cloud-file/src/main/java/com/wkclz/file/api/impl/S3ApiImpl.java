package com.wkclz.file.api.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wkclz.file.api.S3Api;
import com.wkclz.file.config.FileConfig;
import com.wkclz.file.domain.ContentTypeEnum;
import com.wkclz.file.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
public class S3ApiImpl implements S3Api {

    @Autowired
    private FileConfig config;

    public String upload(MultipartFile file, String businessType) {

        String endpoint = config.getS3Endpoint();
        String accessKeyId = config.getS3AccessKeyId();
        String secretKey = config.getS3SecretKey();
        String region = config.getS3Region();
        String bucket = config.getS3Bucket();

        String filename = file.getOriginalFilename();
        assert filename != null;
        int i = filename.lastIndexOf(".");
        String subName = i > 0 ? filename.substring(i) : null;
        String key = OssUtil.getFullName(businessType, filename);

        // 通过 accessKeyId、secretKey 生成认证的aws凭证对象
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        AwsClientBuilder.EndpointConfiguration endpointRegion = new AwsClientBuilder.EndpointConfiguration(endpoint, region);

        // 通过凭证对象 awsCredentials 跟 区域 region 生成s3的服务端(ap-east-1亚太地区香港的区域)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(endpointRegion)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();

        // 设置文件元数据
        // ContentType: 默认不设置的话是什么ostream什么之类的访问链接的话就会下载文件
        ObjectMetadata metadata = new ObjectMetadata();
		//metadata.setContentType(file.getContentType());
        metadata.setContentType(ContentTypeEnum.getContentTypeBySubName(subName));
        metadata.setContentLength(file.getSize());
        try {
            // 想要通过公网访问到图片,除了将桶的权限更改之外，还需要设置withCannedAcl(CannedAccessControlList.PublicRead)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(String.format("Upload file [%s] to AWS S3 failed! Error: %s", key, e.getMessage()));
        } finally {
            s3.shutdown();
        }
        URL url = s3.getUrl(bucket, key);
        return url.toString();
    }
}
