package com.cloud.encrypting_cloud_storage.models.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/19
 * @version: 1.0
 */
@Entity
@Table(name = "file_block", schema = "cloud")
@ApiModel(value = "文件块实体",description = "文件块元数据存储")
@Getter
@Setter
//@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"parentFilePo"})
public class FileBlockPo {
    @Id
    @Column(name = "block_inode")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键，文件唯一标识符")
    private Long blockInode;
    @Basic
    @Column(name = "fingerprint")
    @ApiModelProperty(value = "文件指纹")
    private String fingerprint;
    @Basic
    @Column(name = "idx")
    @ApiModelProperty(value = "文件块位置")
    private Integer idx;
    @Basic
    @Column(name = "size")
    @ApiModelProperty(value = "文件块大小")
    private Long size;
    @Basic
    @Column(name = "bucket")
    @ApiModelProperty(value = "文件块所属桶")
    private String bucket;
    @ManyToOne
    @JoinColumn(name = "file_inode", referencedColumnName = "inode")
    @ApiModelProperty(value = "文件块所属文件")
    private FilePo parentFilePo;

    @ApiModelProperty(value = "文件块链接")
    @Transient
    private String url;

    @Transient
    @JsonIgnore
    private byte[] data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileBlockPo that = (FileBlockPo) o;
        return blockInode != null && Objects.equals(blockInode, that.blockInode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
