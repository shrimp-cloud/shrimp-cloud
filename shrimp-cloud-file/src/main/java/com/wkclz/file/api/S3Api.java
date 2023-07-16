package com.wkclz.file.api;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Api {

    String upload(MultipartFile file, String businessType);

    Integer delete(String objectName);

    Integer delete(List<String> objectNames);

}
