package com.cloud.encrypting_cloud_storage.models.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author leon
 * @Description: 登录授权传输实体
 * @date 2022年04月30日 17:04
 */
@Data
@ApiModel(value = "登录授权传输实体", description = "")
public class AuthenticationRequest {
    @Email
    @ApiModelProperty(value = "用户邮箱")
    private String email;
    @NotNull
    @ApiModelProperty(value = "用户验证哈希", dataType = "String")
    private String verifyKey;
}
