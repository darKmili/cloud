package com.cloud.encrypting_cloud_storage.service;

import com.cloud.encrypting_cloud_storage.models.po.UserPo;

/**
 * @author leon
 * @Description: 用户业务接口
 * @date 2022年04月08日 19:07
 */
public interface UserService {
    /**
     * 新增用户
     * @param userPo
     * @return
     */
    UserPo save(UserPo userPo);

    /**
     * 删除用户
     * @param userPo
     */
    void delete(UserPo userPo);

    /**
     * 通过邮箱获取用户
     * @param email
     * @return
     */
    UserPo findUserByEmail(String email);

    /**
     * 通过邮箱获取用户随机码
     * @param email
     * @return
     */
    String findRandomValueByEmail(String email);

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    UserPo findById(Long id);

    /**
     * 通过邮箱获取用户身份验证码
     * @param email
     * @return
     */
    String findSha256VerifyValueByEmail(String email);

    /**
     * 初始化用户
     * @param userPo 未初始化用户
     */
    UserPo initUser(UserPo userPo);

    /**
     * 获取当前使用容量
     * @param email
     * @return
     */
    long findUsedCapacity(String email);


}
