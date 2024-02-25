package com.ly.ssyxsystem.product.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {


    String fileUpload(MultipartFile file);
}
