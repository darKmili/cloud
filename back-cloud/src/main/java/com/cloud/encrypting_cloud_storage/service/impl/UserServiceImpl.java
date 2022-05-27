package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.enums.FileType;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.repository.FileRepository;
import com.cloud.encrypting_cloud_storage.repository.UserRepository;
import com.cloud.encrypting_cloud_storage.service.UserService;
import com.cloud.encrypting_cloud_storage.util.MyStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author leon
 * @Description: 用户服务
 * @date 2022年04月08日 19:18
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    FileRepository fileRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }


    /**
     * 对于save方法的解释：如果执行此方法是对象中存在id属性，即为更新操作会先根据id查询，再更新
     * 如果执行此方法中对象中不存在id属性，即为保存操作
     *
     * @param userPo
     * @return
     */
    @Override
    public UserPo save(UserPo userPo) {
        return userRepository.save(userPo);
    }

    @Override
    public void delete(UserPo userPo) {
        userRepository.delete(userPo);
    }

    @Override
    public UserPo findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String findRandomValueByEmail(String email) {
        return userRepository.findRandomValueByEmail(email);
    }

    @Override
    public String findSha256VerifyValueByEmail(String email) {
        return userRepository.findVerifyValueByEmail(email);
    }

    /**
     * 初始化用户数据,将用户插入到数据库中，为用户创建目录
     *
     * @param userPo 未初始化用户
     * @return user
     */
    @Override
    public UserPo initUser(UserPo userPo) {
        // 保存多对一的多方要先保存


        userPo.setName(MyStringUtil.getStringRandom(10));
        userPo.setRegisterTime(Timestamp.valueOf(LocalDateTime.now()));
        userPo.setCurLoadTime(userPo.getRegisterTime());
        // 10G
        userPo.setTotalCapacity(10*1024*1024*1024L);
        userPo.setUsedCapacity(0L);
        UserPo save = userRepository.save(userPo);

        FilePo filePo = new FilePo();
        filePo.setType(FileType.DIR);
        filePo.setUser(save);
        filePo.setFilename(userPo.getName());
        filePo.setParentDir(new FilePo(0L));
        fileRepository.save(filePo);

        return save;
    }

    @Override
    public UserPo findById(Long id) {
        Optional<UserPo> byId = userRepository.findById(id);
        return byId.orElse(null);
    }
}
