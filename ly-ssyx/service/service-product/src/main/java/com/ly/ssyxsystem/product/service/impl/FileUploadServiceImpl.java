package com.ly.ssyxsystem.product.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.ly.ssyxsystem.product.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Value("${aliyun.keyid}")
    private String keyid;

    @Value("${aliyun.keysecret}")
    private String keySecret;

    @Value("${aliyun.bucketname}")
    private String bucketName;

    @Override
    public String fileUpload(MultipartFile file) {

        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        String objectName = "exampledir/exampleobject.txt";

        String url = null;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keySecret);

        try {
            // 填写Byte数组
            InputStream inputStream = file.getInputStream();

            String objectName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            objectName = uuid + objectName;
            // 对上传文件进行分组，根据当前年/月/日
            String currentDateTime = new DateTime().toString("yyyy/MM/dd");
            objectName = currentDateTime + "/" + objectName;

            // 创建PutObjectRequest对象。
            // 1、bucket名称
            // 2、上传文件的路径+名称
            // 3、上传文件的输入流
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName
                    , objectName,
                    inputStream);
            // 设置该属性可以返回response，如果不设置，则返回的response为空
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("文件上传最后的状态码：{}", result.getResponse().getStatusCode());
            log.info("文件上传最后的uri：{}", result.getResponse().getUri());
            // 返回上传图片在阿里云的路径
            url = result.getResponse().getUri();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }
}
