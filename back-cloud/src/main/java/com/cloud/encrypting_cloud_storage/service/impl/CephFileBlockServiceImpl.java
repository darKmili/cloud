package com.cloud.encrypting_cloud_storage.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    public boolean uploadBlock(FileBlockPo fileBlockPo) throws Exception {
        List<Bucket> buckets = amazonS3.listBuckets();
        InputStream inputStream = new ByteArrayInputStream(fileBlockPo.getData());
        PutObjectResult putObjectResult = amazonS3.putObject(bucketName, fileBlockPo.getFingerprint(), inputStream, null);
        return putObjectResult.isRequesterCharged();
    }

    @Override
    public byte[] downloadBlock(FileBlockPo fileBlockPo) throws Exception {
        String fileName = fileBlockPo.getFingerprint();
//        S3Object object;
//        if (amazonS3.doesObjectExist(bucketName, fileName)) {
//            object = amazonS3.getObject(bucketName, fileName);
//        } else {
//            throw new ApiException(404,"文件不存在");
//        }
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

    /**
     * 检查储存空间是否已创建
     *
     */
    private void checkBucket() {

        if (amazonS3.doesBucketExist(bucketName)) {
            log.debug("Storage s3 api, bucketName is found: " + bucketName);
        } else {
            log.warn("Storage s3 api, bucketName is not exist, create it: " + bucketName);
            amazonS3.createBucket(bucketName);
        }
    }
}
