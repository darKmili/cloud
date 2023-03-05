package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.ShareFilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShareFilePoRepository extends JpaRepository<ShareFilePo, Long> {

    /**
     * 根据分享者获取该分享者所有分享的分享数据
     * @return
     */
    List<ShareFilePo> findBySharerUser(UserPo sharerUser);

    /**
     * 根据被分享者获取该被分享者所有被分享的分享数据
     * @return
     */
    List<ShareFilePo> findByTargetUser(UserPo targetUser);
}