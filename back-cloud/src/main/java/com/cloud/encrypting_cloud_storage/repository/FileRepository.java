package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * @author leon
 * @Description: 文件持久层
 * @date 2022年04月08日 17:44
 */

public interface FileRepository extends JpaRepository<FilePo, Long> {


    /**
     * 判断是否存在文件
     *
     * @param inode
     * @return
     */
    boolean existsByInode(Long inode);




    FilePo findByInode(Long inode);

    /**
     * 根据用户的ID获取所有的文件
     *
     * @param userPo
     * @return
     */
    // @Query(value = "SELECT * FROM file WHERE file.user_id=?1",nativeQuery = true)
    @EntityGraph(value = "filePo.find",type = EntityGraph.EntityGraphType.FETCH)
    Set<FilePo> findByUser(UserPo userPo);



    /**
     * 获取当前目录下的文件和文件夹
     *
     * @param parentInode
     * @return
     */
    @Query(value = "SELECT * FROM file WHERE file.parent_inode=?1",nativeQuery = true)
    List<FilePo> findFilePosByParentInode(Long parentInode);

    /**
     * 获取指定用户，指定目录下的所有文件
     *
     * @param userPo
     * @param parentDir
     * @return
     */
    List<FilePo> findAllByUserAndParentDir(UserPo userPo, FilePo parentDir);

    /**
     * 删除指定用户的指定文件
     *
     * @param inode  文件inode
     * @param userPo 用户
     */
    void deleteByInodeAndUser(Long inode, UserPo userPo);

    FilePo findByInodeAndUser(Long inode, UserPo userPo);



}
