package com.wkclz.file.api;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AliOssApi {

    String upload(MultipartFile file, String businessType);

    Integer delete(String objectName);

    Integer delete(List<String> objectNames);

}
