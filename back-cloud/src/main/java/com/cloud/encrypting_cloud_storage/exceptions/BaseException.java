package com.cloud.encrypting_cloud_storage.exceptions;

import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author leon
 * @Description: 业务异常
 * @date 2022年04月25日 18:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException{
    private Integer code;
    private String message;
    public BaseException(Integer code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }
    public BaseException(StatusEnum status){
        super(status.getMassage());
        this.code = status.getCode();
        this.message = status.getMassage();
    }
}
