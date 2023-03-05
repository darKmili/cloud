package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.ShareFilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2023/2/25
 * @version: 1.0
 */
public interface ShareFileService {

    /**
     * 创建一个文件分享信息
     */
    ShareFilePo createShareFile(UserPo sharer, UserPo targetUser, FilePo filePo);


    List<Map> getByShareFiles(UserPo targetUser);
    List<Map> getShareFiles(UserPo sharerUser);
}
