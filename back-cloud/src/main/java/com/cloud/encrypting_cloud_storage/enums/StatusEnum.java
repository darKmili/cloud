package com.cloud.encrypting_cloud_storage.enums;

/**
 * @ClassName: ExceptionEnum
 * @Description: 统一状态枚举
 * @author: li
 * @Date: 2022/4/20 11:17 上午
 */
public enum StatusEnum {
    /**
     * 请求成功
     */
    OK(2000, "请求成功"),
    /**
     * 未登录
     */
    NOT_LOGIN(4001, "未登录"),
    /**
     * 没有权限
     */
    NO_PERMISSIONS(4002, "缺少权限!"),

    INVALID_REQUEST(40003, "无效请求"), NO_USER(40004, "无此用户"), NO_RESOURCES(4005, "无此资源"), HAVE_USER(4006, "用户已经注册，无需注册"),

    /**
     * 服务端的故障
     */
    SERVER_ERROR(5000, "服务端故障!");

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误描述
     */
    private final String massage;

    StatusEnum(Integer code, String msg) {
        this.code = code;
        this.massage = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMassage() {
        return massage;
    }

}
