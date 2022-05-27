package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author leon
 * @Description: 文件块持久层
 * @date 2022年04月08日 17:40
 */
public interface FileBlockRepository extends JpaRepository<FileBlockPo,Long> {
}
