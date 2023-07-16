package com.wkclz.file.api;

import org.springframework.web.multipart.MultipartFile;

public interface FileApi {

    String upload(MultipartFile file, String businessType);
}
