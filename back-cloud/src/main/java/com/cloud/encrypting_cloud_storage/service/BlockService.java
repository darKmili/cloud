package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;

/**
 * @author leon
 * @Description: 文件块服务，主要负责文件块的存取，文件块的查询
 * @date 2022年05月01日 14:13
 */
public interface BlockService {
    /**
     *
     * @param fileBlockPo 文件块
     * @return
     * @throws Exception
     */
    boolean uploadBlock(FileBlockPo fileBlockPo) throws Exception;


    FileBlockPo save(FileBlockPo fileBlockPo);
}
