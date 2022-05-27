package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.enums.FileState;
import com.cloud.encrypting_cloud_storage.enums.FileType;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/20
 * @version: 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileRepositoryTest {
    @Autowired
    FileRepository fileRepository;

    @Test
    void findFilePosByFileByParentInode() {

        List<FilePo> filePosByFileByParentInode = fileRepository.findFilePosByParentInode(0L);
        assert filePosByFileByParentInode != null;
    }

//    @Transactional
    @Test
    void save() {
        FilePo newFile = new FilePo(11L);
        newFile.setState(FileState.NEW);
        newFile.setType(FileType.FILE);
        newFile.setFilename("再次改名");
//        // 新文件，必定有目录和所属用户
//        FilePo dir = new FilePo(2L);
//        UserPo userPo = new UserPo(1L);
//        newFile.setParentDir(dir);
//        newFile.setUser(userPo);

        fileRepository.saveAndFlush(newFile);

//        List<FilePo> allByUserAndParentDir = fileRepository.findAllByUserAndParentDir(userPo, dir);

//        assert allByUserAndParentDir!=null;
    }

    @Test
    void existsByInode() {
        boolean b = fileRepository.existsByInode(0L);
        System.out.println(b);
    }

    @Test
    void findAllByUser() {
        Set<FilePo> user = fileRepository.findByUser(new UserPo(1L));
        assert user != null;
    }

    @Test
    void findAllByUserAndParentDir() {
        List<FilePo> allByUserAndParentDir = fileRepository.findAllByUserAndParentDir(new UserPo(1L), new FilePo(1L));
        assert allByUserAndParentDir != null;
    }


    @Transactional(rollbackFor = Exception.class)
    @Test
    void deleteByInodeAndUser() {
        FilePo filePo = fileRepository.findByInodeAndUser(2L, new UserPo(1L));
        fileRepository.deleteByInodeAndUser(2L, new UserPo(1L));
        filePo = fileRepository.findByInodeAndUser(2L, new UserPo(1L));
        assert filePo == null;

    }

    @Test
    void findByInodeAndUser() {
        FilePo filePo = fileRepository.findByInodeAndUser(4L, new UserPo(1L));
        assert filePo != null;
    }

    @Test
    void findByInode() {
        FilePo filePo = fileRepository.findByInode(0L);
        assert filePo != null;
    }

}