package com.cloud.encrypting_cloud_storage.exceptions;

import com.cloud.encrypting_cloud_storage.enums.StatusEnum;

/**
 * @ClassName: JsonException
 * @Description: Json异常
 * @author: leon
 * @date 2022/4/20 11:20 上午
 */
public class ApiException extends BaseException {
    public ApiException(Integer code, String message) {
        super(code, message);
    }

    public ApiException(StatusEnum status) {
        super(status);
    }
}