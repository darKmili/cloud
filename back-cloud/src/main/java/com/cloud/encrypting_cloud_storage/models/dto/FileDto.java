package com.cloud.encrypting_cloud_storage.models.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon @description： @date： 2022/5/26
 * 
 * @version: 1.0
 */
@Data
public class FileDto {
    private String filename;
    private Long size;
    private Long blockSize;
    private String fileKey;
    private Long userId;
    private Long parentInode;
    private String mtime;
}
