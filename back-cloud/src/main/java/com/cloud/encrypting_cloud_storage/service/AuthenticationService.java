package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.dto.AuthenticationRequest;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;

/**
 * @author leon
 * @Description: TODO(身份认证)
 * @date 2022年04月22日 10:33
 */
public interface AuthenticationService {
    UserPo authentication(AuthenticationRequest authenticationRequest);
    UserPo findUserByEmail(String email);
}
