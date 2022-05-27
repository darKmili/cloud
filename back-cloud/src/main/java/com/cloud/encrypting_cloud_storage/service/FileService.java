package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;

import java.util.List;
import java.util.Set;

/**
 * @author leon
 * @Description: 上传服务
 * @date 2022年04月26日 13:55
 */
public interface FileService {
    /**
     * 文件上传接口
     * @param filePo 文件
     * @return API
     * @throws Exception　异常
     */
    boolean uploadFile(FilePo filePo) throws Exception;


    /**
     * 判断文件是否存在
     * @param inode
     * @return
     */
    boolean exitsMyFile(Long inode);

    /**
     * 获取用户指下面的所有文件
     * @param userPo
     * @return
     */
    Set<FilePo> findAllFile(UserPo userPo);

    /**
     * 获取指定用户，指定目录下面的所有文件
     * @param userPo 用户
     * @param dir 目录
     * @return 所有文件目录
     */
    List<FilePo> findAllFileByUserAndDir(UserPo userPo,FilePo dir);

    /**
     * 删除文件
     * @param inode 文件inode
     * @param userId 用户id
     */
    void deleteFile(Long inode,Long userId);

    /**
     * 查询指定用户，指定Inode的目录
     * @param inode
     * @param userId
     * @return
     */
    FilePo findFileByInodeAndUserId(Long inode, Long userId);

    FilePo save(FilePo filePo);




    void deleteByInodeAndUser(Long inode, Long userId);
}
