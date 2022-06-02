package com.cloud.encrypting_cloud_storage.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author leon
 * @Description: 文件块服务
 * @date 2022年05月30日 21:13
 */
@Service("cephFileBlockService")
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
        InputStream inputStream =new  ByteArrayInputStream(fileBlockPo.getData());
        PutObjectResult putObjectResult = amazonS3.putObject(bucketName, fileBlockPo.getFingerprint(), inputStream, null);
        return putObjectResult.isRequesterCharged();
    }

    @Override
    public URL downloadBlock(FileBlockPo fileBlockPo) throws Exception {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileBlockPo.getFingerprint());
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

    }
}
