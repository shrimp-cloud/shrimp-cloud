package com.wkclz.file.api;

import org.springframework.web.multipart.MultipartFile;

public interface S3Api {

    String upload(MultipartFile file, String businessType);
}
