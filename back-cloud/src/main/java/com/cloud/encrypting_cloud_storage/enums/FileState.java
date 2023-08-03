package com.cloud.encrypting_cloud_storage.enums;

/**
 * @author leon
 * @Description: 文件或者目录的生命周期枚举
 * @date 2022年05月10日 16:45
 */
public enum FileState {
    /**
     * 新建状态
     */
    NEW("NEW"),
    /**
     * 正在上传
     */
    UPLOADING("UPLOADING"),
    /**
     * 已经上传完成
     */
    UPLOADED("UPLOADED"),
    /**
     * 销毁或者回收站状态
     */
    DESTROY("DESTROY");

    private FileState(String state) {
        this.state = state;
    }

    private String state;

    public String getState() {
        return state;
    }
}
