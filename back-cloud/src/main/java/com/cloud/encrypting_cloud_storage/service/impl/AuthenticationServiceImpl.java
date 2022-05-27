package com.cloud.encrypting_cloud_storage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.dto.AuthenticationRequest;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.service.AuthenticationService;
import com.cloud.encrypting_cloud_storage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author leon
 * @Description: TODO
 * @date 2022年04月22日 10:35
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    @Resource
    UserService userService;

    @Override
    public UserPo authentication(AuthenticationRequest authenticationRequest) {
        String email = authenticationRequest.getEmail();
        String verifyHash = authenticationRequest.getVerifyKey();
        if(StrUtil.isBlank(email)){
            throw new ApiException(-1,"账号不能为空");
        }
        UserPo userPo = findUserByEmail(authenticationRequest.getEmail());
        if(userPo ==null){
            throw new ApiException(-1,"用户不存在");
        }
        if(!StrUtil.equals(verifyHash, userPo.getVerifyKey())){
            throw new ApiException(-1,"密码错误");
        }
        return userPo;
    }

    @Override
    public UserPo findUserByEmail(String email) {
        return userService.findUserByEmail(email);
    }
}
