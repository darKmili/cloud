package com.cloud.encrypting_cloud_storage.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.util.MyStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;


/**
 * @author leon
 * @Description: 文件块服务
 * @date 2022年05月30日 21:13
 */
@Transactional(rollbackFor = Exception.class)
@Service("cephFileBlockService")
@Slf4j
public class CephFileBlockServiceImpl extends BlockServiceImpl {

    private String bucketName;
    private AmazonS3 amazonS3;

    @Value("${ceph.bucket}")
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Autowired
    public void setAmazonS3(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadBlock(FileBlockPo fileBlockPo) throws Exception {

        uploadBlock(fileBlockPo.getData(), fileBlockPo.getFingerprint());
        fileBlockPo.setBucket(bucketName);
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000 * 60 * 60 * 3;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileBlockPo.getFingerprint())
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        fileBlockPo.setUrl(url.toString());
        return url.toString();
    }

    @Async
    public void uploadBlock(byte[] data, String fingerprint) throws Exception {
        fingerprint = Base64.encodeAsString(fingerprint.getBytes());
        InputStream inputStream = new ByteArrayInputStream(data);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(inputStream.available());
        PutObjectResult putObjectResult = amazonS3.putObject(bucketName, fingerprint, inputStream, objectMetadata);
        log.info(putObjectResult.toString());
    }

    @Override
    public byte[] downloadBlock(FileBlockPo fileBlockPo) throws Exception {
        String fileName = Base64.encodeAsString(fileBlockPo.getFingerprint().getBytes());

        S3Object object = amazonS3.getObject(bucketName, fileName);
        log.debug("Storage s3 api, get object result :{}", object);

        byte[] fileByte = null;
        InputStream inputStream;
        inputStream = object.getObjectContent();
        try {
            fileByte = IOUtils.toByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        log.info("Storage s3 api, download file end");
        return fileByte;
    }


    /**
     * inputStream转byte[]
     *
     * @param inStream 输入
     * @param fileSize 文件大小
     * @return 输出
     * @throws IOException 异常
     */
    private static byte[] inputToByte(InputStream inStream, int fileSize) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[fileSize];
        int rc;
        while ((rc = inStream.read(buff, 0, fileSize)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }



    @Override
    @Async("defaultThreadPoolExecutor")
    public void deleteBlock(FileBlockPo fileBlockPo) {

        String fileName = Base64.encodeAsString(fileBlockPo.getFingerprint().getBytes());
        try {
            amazonS3.deleteObject(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String getFingerprintUrl(String fingerprint) {
        String fileName = Base64.encodeAsString(fingerprint.getBytes());
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000 * 60 * 60 * 3;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}
