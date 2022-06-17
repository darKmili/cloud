package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;

import java.net.URL;
import java.util.Set;

/**
 * @author leon
 * @Description: 文件块服务，主要负责文件块的存取，文件块的查询
 * @date 2022年05月01日 14:13
 */
public interface BlockService {
    /**
     * 上传文件块到云平台
     * @param fileBlockPo 文件块
     * @return
     * @throws Exception
     */
    boolean uploadBlock(FileBlockPo fileBlockPo) throws Exception;

    /**
     * 下载文件块数据
     * @param fileBlockPo
     * @return
     * @throws Exception
     */
    byte[] downloadBlock(FileBlockPo fileBlockPo) throws Exception;

    /**
     * 保存文件块信息到数据库
     * @param fileBlockPo
     * @return
     */
    FileBlockPo save(FileBlockPo fileBlockPo);

    /**
     * 找到文件的所有块
     * @param filePo
     * @return
     */
    Set<FileBlockPo> findFileAllBlock(FilePo filePo);
}
