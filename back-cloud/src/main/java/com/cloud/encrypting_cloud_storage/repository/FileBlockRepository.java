package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * @author leon
 * @Description: 文件块持久层
 * @date 2022年04月08日 17:40
 */
public interface FileBlockRepository extends JpaRepository<FileBlockPo,Long> {
    /**
     * 找到当前文件的所有块
     * @param filePo
     * @return
     */
    Set<FileBlockPo> findByParentFilePo(FilePo filePo);


    boolean existsByFingerprint(String fingerprint);
}
