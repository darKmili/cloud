package com.cloud.encrypting_cloud_storage.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.repository.FileBlockRepository;
import com.cloud.encrypting_cloud_storage.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/24
 * @version: 1.0
 */
public abstract class BlockServiceImpl implements BlockService {
    @Autowired
    FileBlockRepository fileBlockRepository;



    @Override
    public FileBlockPo save(FileBlockPo fileBlockPo) {
        return fileBlockRepository.save(fileBlockPo);
    }

    @Override
    public Set<FileBlockPo> findFileAllBlock(FilePo filePo) {
        return fileBlockRepository.findByParentFilePo(filePo);
    }



    @Override
    public boolean existsByFingerprint(String fingerprint){
        return fileBlockRepository.existsByFingerprint(fingerprint);
    }




}
