package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/23
 * @version: 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest {
    String EMAIL = "1227642494@qq.com";

    @Autowired
    UserRepository userRepository;
    @Test
    void findUserByName() {
        UserPo leon = userRepository.findUserByName("leon");
        assert leon!=null;
    }
    @Test
    void findUserByEmail(){
        UserPo userByEmail = userRepository.findByEmail(EMAIL);
        assert userByEmail!=null;
    }
    @Test
    void findRandomValueByEmail(){
        String randomValueByEmail = userRepository.findRandomValueByEmail(EMAIL);
        System.out.println(randomValueByEmail);
    }

    @Test
    void findVerifyValueByEmail(){
        String verifyValueByEmail = userRepository.findVerifyValueByEmail(EMAIL);
        System.out.println(verifyValueByEmail);
    }
    @Test
    void findById(){
        Optional<UserPo> byId = userRepository.findById(1L);
        assert byId!=null;
    }
}