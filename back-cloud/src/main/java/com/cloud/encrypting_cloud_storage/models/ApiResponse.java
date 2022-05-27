package com.cloud.encrypting_cloud_storage.models;


import com.cloud.encrypting_cloud_storage.enums.StatusEnum;
import com.cloud.encrypting_cloud_storage.exceptions.BaseException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * //Accessors(chain = true) 链式调用set
 * @author leon
 * @Description: 通用的API接口封装
 * @date 2022年04月20日 12:34
 */

@Data
@Accessors(chain = true)
@ApiModel(value = "用户通用API接口返回")
public class ApiResponse {
    /**
     * 状态码
     */
    @ApiModelProperty(value = "响应状态码",required = true)
    private Integer code;
    /**
     * 返回内容
     */
    @ApiModelProperty(value = "响应消息码",required = true)
    private String message;
    /**
     * 返回数据
     */
    @ApiModelProperty(value = "响应数据",required = true)
    private Object data;

    /**
     * 无参构造
     */
    public ApiResponse() {

    }

    /**
     * 全参构造
     *
     * @param code    状态码
     * @param message 返回信息
     * @param data    返回数据
     */
    public ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        
        this.data = data;
    }

    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回信息
     * @param data    返回数据
     * @return apiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * 一个成功并且有数据的API
     *
     * @param data 数据
     * @return API
     */
    public static ApiResponse ofSuccess(Object data) {
        return ofStatus(StatusEnum.OK, data);
    }

    /**
     * 一个成功没有数据的API
     *
     * @return API
     */
    public static ApiResponse ofSuccess() {
        return ofSuccess(null);
    }

    /**
     * 一个带状态无数据的API
     * @param status 状态
     * @return API
     */
    public static ApiResponse ofStatus(StatusEnum status) {
        return ApiResponse.ofStatus(status, null);
    }

    /**
     * 一个带状态带数据的API
     * @param status　状态
     * @param data　数据
     * @return API
     */
    public static ApiResponse ofStatus(StatusEnum status, Object data) {
        return ApiResponse.of(status.getCode(), status.getMassage(), data);
    }

    /**
     * 一个成功，自定义信息，无数据的API
     * @param message 自定义信息
     * @return API
     */
    public static ApiResponse ofMessage(String message) {
        return ApiResponse.of(StatusEnum.OK.getCode(), message, null);
    }

    /**
     * 一个成功，自定义信息，带数据的API
     * @param message 自定义信息
     * @param data 数据
     * @return API
     */
    public static ApiResponse ofMessage(String message,Object data) {
        return ApiResponse.of(StatusEnum.OK.getCode(), message, data);
    }

    /**
     * 一个带异常，带数据的API
     * @param t 异常
     * @param data 数据
     * @param <T> {@link BaseException} 的子类
     * @return API
     */
    public static <T extends BaseException> ApiResponse ofException(T t, Object data) {
        return of(t.getCode(), t.getMessage(), data);
    }

    /**
     * 一个带异常，无数据的API
     * @param t 异常
     * @param <T> {@link BaseException} 的子类
     * @return API
     */
    public static <T extends BaseException> ApiResponse ofException(T t) {
        return of(t.getCode(), t.getMessage(), null);
    }


}
