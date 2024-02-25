package com.ly.ssyxsystem.product.controller;


import com.ly.ssyxsystem.product.service.FileUploadService;
import com.ly.ssyxsystem.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件上传")
@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    // 图片上传的方法
    @ApiOperation("图片上传")
    @PostMapping("fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.fileUpload(file);
        return Result.ok(url);
    }

}
