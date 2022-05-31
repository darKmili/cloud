package com.cloud.encrypting_cloud_storage.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    public FileBlockPo downloadBlock(FileBlockPo fileBlockPo) throws Exception {
        S3Object object = amazonS3.getObject(bucketName, fileBlockPo.getFingerprint());
        S3ObjectInputStream objectContent = object.getObjectContent();
        InputStream delegateStream = objectContent.getDelegateStream();
        int b;
        List<Byte> res = new ArrayList<>();
        while ((b=delegateStream.read())!=-1){
            res.add((byte) b);
        }
        byte[] bytes = new byte[res.size()];
        for (int i = 0; i < res.size(); i++) {
            bytes[i] = res.get(i);
        }

        fileBlockPo.setData(bytes);
        return super.downloadBlock(fileBlockPo);
    }
}
