package com.cloud.encrypting_cloud_storage.models.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/26
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BlockVo {
    private String opt;

    private Integer next;
}
