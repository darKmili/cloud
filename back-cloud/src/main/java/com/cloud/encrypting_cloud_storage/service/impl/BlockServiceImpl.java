package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.repository.FileBlockRepository;
import com.cloud.encrypting_cloud_storage.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/24
 * @version: 1.0
 */
public class BlockServiceImpl implements BlockService {
    @Autowired
    FileBlockRepository fileBlockRepository;
    @Override
    public boolean uploadBlock(FileBlockPo fileBlockPo) throws Exception {
        return false;
    }

    @Override
    public FileBlockPo save(FileBlockPo fileBlockPo) {
        return fileBlockRepository.save(fileBlockPo);
    }
}
