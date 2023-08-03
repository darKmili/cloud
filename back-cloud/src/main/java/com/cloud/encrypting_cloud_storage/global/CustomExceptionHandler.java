package com.cloud.encrypting_cloud_storage.global;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 * @Description: TODO(统一异常处理)
 * @date 2022年04月20日 12:16
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    /**
     * 统一 JSON 异常处理
     *
     * @param apiException
     * @return
     */
    @ExceptionHandler({ApiException.class})
    public ApiResponse jsonErrorHandler(ApiException apiException) {
        return ApiResponse.ofException(apiException);
    }

}
