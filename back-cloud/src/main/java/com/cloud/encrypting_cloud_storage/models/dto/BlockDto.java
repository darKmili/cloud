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
public class BlockDto {
    private String fingerprint;
    private Integer idx;
    private Long size;
}
