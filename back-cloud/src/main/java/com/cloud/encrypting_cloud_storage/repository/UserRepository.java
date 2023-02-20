package com.cloud.encrypting_cloud_storage.repository;

import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author leon
 * @Description: 用户持久层
 * @date 2022年04月08日 17:12
 */
public interface UserRepository extends JpaRepository<UserPo, Long>, JpaSpecificationExecutor<UserPo> {
    /**
     * 通过用户名获取用户
     *
     * @param name
     * @return
     */
    @EntityGraph(value = "userpo.all",type = EntityGraph.EntityGraphType.FETCH)
    UserPo findUserByName(String name);

    /**
     * 通过邮箱获取用户
     * @param email
     * @return
     */
    UserPo findByEmail(String email);

    /**
     * 通过邮箱获取用户随机码
     * @param email 邮箱账号
     * @return 随机码
     */
    @Query(value = "select u.client_random_value from user as u where u.email=?1", nativeQuery = true)
    String findRandomValueByEmail(String email);

    /**
     * 通过邮箱获取身份验证码
     * @param email 邮箱
     * @return 身份验证码
     */
    @Query(value = "select u.sha256verify_Key from user as u where u.email=?1", nativeQuery = true)
    String findVerifyValueByEmail(String email);

    /**
     * 删除用户，通过用户邮箱
     * @param email 邮箱
     */
    void deleteUserByEmailIs(String email);

    @Query(value = "select u.used_capacity from user as u where u.email=?1",nativeQuery = true)
    long findUsedCapacityByEmail(String email);








}
