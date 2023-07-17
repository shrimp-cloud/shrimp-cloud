package com.wkclz.file.api.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.wkclz.file.api.AliOssApi;
import com.wkclz.file.api.FileApi;
import com.wkclz.file.config.FileAliOssConfig;
import com.wkclz.file.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("AliOssApi")
public class AliOssApiImpl implements FileApi, AliOssApi {

    @Autowired
    private FileAliOssConfig config;

    @Override
    public String upload(MultipartFile file, String businessType) {

        String bucket = config.getBucket();
        String endpoint = config.getEndpoint();
        String bucketDomain = config.getBucketDomain();
        String accessKeyId = config.getAccessKeyId();
        String accessKeySecret = config.getSecretKeySecret();

        DefaultCredentialProvider credentialProvider = getCredentialProvider(accessKeyId, accessKeySecret);
        String objectName = OssUtil.getFullName(businessType, file.getOriginalFilename());

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialProvider);

        try {
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucket, objectName, inputStream);
        } catch (OSSException oe) {
            log.error("upload file to ali oss faild: errCode: {}, requestId: {}, hostId: {}, msg: {}",
                oe.getErrorCode(), oe.getRequestId(), oe.getHostId(), oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("upload file to ali oss faild: {}", ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 此处返回外网地址
        if (StringUtils.isBlank(bucketDomain)) {
            bucketDomain = endpoint;
        }
        if (!bucketDomain.endsWith("/")) {
            bucketDomain = bucketDomain + "/";
        }
        return bucketDomain + objectName;
    }


    /**
     * OSS 单文件删除
     */
    public Integer delete(String objectName) {
        List<String> objectNames = new ArrayList<>();
        objectNames.add(objectName);
        return delete(objectNames);
    }

    /**
     * OSS 多文件删除
     */
    public Integer delete(List<String> objectNames) {

        String endpoint = config.getEndpoint();
        String accessKeyId = config.getAccessKeyId();
        String accessKeySecret = config.getSecretKeySecret();
        String bucket = config.getBucket();

        DefaultCredentialProvider credentialProvider = getCredentialProvider(accessKeyId, accessKeySecret);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialProvider);
        try {
            // 删除文件。
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucket);
            request.setKeys(objectNames);
            DeleteObjectsResult objectsResult = ossClient.deleteObjects(request);
            List<String> objects = objectsResult.getDeletedObjects();
            return objects.size();
        } catch (OSSException oe) {
            log.error("delete file from ali oss faild: errCode: {}, requestId: {}, hostId: {}, msg: {}",
                oe.getErrorCode(), oe.getRequestId(), oe.getHostId(), oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("delete file from ali oss faild: {}", ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return 0;
    }

    private static DefaultCredentialProvider getCredentialProvider(String accessKeyId, String accessKeySecret) {
        return CredentialsProviderFactory.newDefaultCredentialProvider(accessKeyId, accessKeySecret);
    }

}