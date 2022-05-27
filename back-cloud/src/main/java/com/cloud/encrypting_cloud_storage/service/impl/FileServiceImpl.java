package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.repository.FileRepository;
import com.cloud.encrypting_cloud_storage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author leon
 * @Description: 文件块服务
 * @date 2022年05月02日 13:36
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl implements FileService {
    FileRepository fileRepository;
    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public boolean uploadFile(FilePo filePo) throws Exception {
        FilePo save = fileRepository.save(filePo);
        return save!=null;
    }


    @Override
    public boolean exitsMyFile(Long inode) {
        return fileRepository.existsByInode(inode);
    }

    @Override
    public Set<FilePo> findAllFile(UserPo userPo) {
        return fileRepository.findByUser(userPo);
    }

    @Override
    public List<FilePo> findAllFileByUserAndDir(UserPo userPo, FilePo dir) {
        return fileRepository.findAllByUserAndParentDir(userPo,dir);
    }

    @Override
    public void deleteFile(Long inode, Long userId) {
        fileRepository.deleteByInodeAndUser(inode,new UserPo(userId));
    }

    @Override
    public FilePo findFileByInodeAndUserId(Long inode, Long userId) {
        return fileRepository.findByInodeAndUser(inode,new UserPo(userId));
    }

    @Override
    public FilePo save(FilePo filePo) {
        return fileRepository.saveAndFlush(filePo);
    }

    @Override
    public void deleteByInodeAndUser(Long inode, Long userId){
        fileRepository.deleteByInodeAndUser(inode,new UserPo(userId));
    }
}
