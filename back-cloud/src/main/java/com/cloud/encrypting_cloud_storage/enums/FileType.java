package com.cloud.encrypting_cloud_storage.enums;

/**
 * @author leon
 * @Description: 文件或者目录的类型 枚举
 * @date 2022年05月10日 16:46
 */
public enum FileType {
    /**
     * 文件
     */
    FILE("FILE"),
    /**
     * 目录
     */
    DIR("DIR");

    private String type;

    private FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
