package com.cloud.encrypting_cloud_storage.models.vo;

import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class BlockVo {
    private String opt;

    private Integer next;
    private Long blockInode;
    private String fingerprint;
    private Integer idx;
    private Long size;
    private FilePo parentFilePo;
    private String data;

    public BlockVo(String opt,Integer next){
        this.opt = opt;
        this.next = next;
    }
}
