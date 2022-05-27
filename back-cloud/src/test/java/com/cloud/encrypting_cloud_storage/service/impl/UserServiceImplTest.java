package com.cloud.encrypting_cloud_storage.service.impl;

import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/20
 * @version: 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceImplTest {
    @Autowired
    UserServiceImpl userService;

    @Transactional(rollbackFor = Exception.class)
    @Test
    void initUser() {
        UserPo userPo = new UserPo();
        userPo.setEmail("1222@qq.com");
        userPo.setClientRandomValue("dassssssssdasdsa");
        userPo.setVerifyKey("sdasdasdasd");
        userPo.setEncryptedMasterKey("dsadasdsadasd");
        userService.initUser(userPo);
    }
}