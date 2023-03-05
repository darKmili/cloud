package com.cloud.encrypting_cloud_storage.controller;

import com.cloud.encrypting_cloud_storage.service.AuthenticationService;
import com.cloud.encrypting_cloud_storage.service.FileService;
import com.cloud.encrypting_cloud_storage.service.ShareFileService;
import com.cloud.encrypting_cloud_storage.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author leon
 * @Description: 基础控制器
 * @date 2022年04月22日 11:21
 */
@Controller
public abstract class BaseController {
    @Value("${user.key}")
    String userKey;
    @Resource
    UserService userService;
    @Resource
    FileService fileService;
    @Resource
    AuthenticationService authenticationService;
    @Resource
    ShareFileService shareFileService;
}
