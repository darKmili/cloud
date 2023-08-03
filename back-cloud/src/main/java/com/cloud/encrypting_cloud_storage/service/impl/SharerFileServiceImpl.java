package com.cloud.encrypting_cloud_storage.service.impl;

import java.util.*;

import org.springframework.stereotype.Service;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.ShareFilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.repository.ShareFilePoRepository;
import com.cloud.encrypting_cloud_storage.service.ShareFileService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author： leon @description： @date： 2023/2/25
 * 
 * @version: 1.0
 */
@Service("文件分享")
@Slf4j
public class SharerFileServiceImpl implements ShareFileService {

    ShareFilePoRepository shareFilePoRepository;

    @Override
    public ShareFilePo createShareFile(UserPo sharer, UserPo targetUser, FilePo filePo) {
        final ShareFilePo shareFilePo = new ShareFilePo();
        shareFilePo.setSharerUser(sharer);
        shareFilePo.setTargetUser(targetUser);
        shareFilePo.setFilePo(filePo);
        return shareFilePoRepository.save(shareFilePo);
    }

    @Override
    public List<Map> getByShareFiles(UserPo targetUser) {
        final List<ShareFilePo> bySharerFile = shareFilePoRepository.findByTargetUser(targetUser);
        List<Map> res = new ArrayList<>();
        for (ShareFilePo shareFilePo : bySharerFile) {
            final FilePo filePo = Objects.requireNonNull(shareFilePo.getFilePo());
            final UserPo sharerUser = Objects.requireNonNull(shareFilePo.getSharerUser());
            final UserPo bySharerUser = Objects.requireNonNull(shareFilePo.getTargetUser());
            Map<String, Object> map = new HashMap<>();
            map.put("filename", filePo.getFilename());
            map.put("inode", filePo.getInode());
            map.put("sharer", sharerUser.getEmail());
            map.put("share_time", shareFilePo.getShareTime());
            map.put("fileKey", filePo.getFileKey());
            map.put("byShareUser", bySharerUser.getEmail());
            res.add(map);
        }
        return res;
    }

    @Override
    public List<Map> getShareFiles(UserPo sharerUser) {
        final List<ShareFilePo> sharerFiles = shareFilePoRepository.findBySharerUser(sharerUser);
        List<Map> res = new ArrayList<>();
        for (ShareFilePo shareFilePo : sharerFiles) {
            final FilePo filePo = Objects.requireNonNull(shareFilePo.getFilePo());
            final UserPo targetUser = Objects.requireNonNull(shareFilePo.getTargetUser());
            final UserPo sharer = Objects.requireNonNull(shareFilePo.getSharerUser());
            Map<String, Object> map = new HashMap<>();
            map.put("filename", filePo.getFilename());
            map.put("inode", filePo.getInode());
            map.put("sharer", sharerUser.getEmail());
            map.put("share_time", shareFilePo.getShareTime());
            map.put("fileKey", filePo.getFileKey());
            map.put("byShareUser", sharer.getEmail());
            res.add(map);
        }
        return res;
    }

}
