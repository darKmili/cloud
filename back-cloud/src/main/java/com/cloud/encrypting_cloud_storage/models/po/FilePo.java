package com.cloud.encrypting_cloud_storage.models.po;

import com.cloud.encrypting_cloud_storage.enums.FileState;
import com.cloud.encrypting_cloud_storage.enums.FileType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2022/5/19
 * @version: 1.0
 */
@Entity
@Table(name = "file", schema = "cloud")
@ApiModel(value = "文件实体", description = "即可表示文件，又可以表示目录")
@NamedEntityGraph(name = "filePo.find", attributeNodes = {@NamedAttributeNode(value = "childrenFiles"), @NamedAttributeNode(value = "fileBlocks")})
@Getter
@Setter
//@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"user","parentDir"})
public class FilePo implements Serializable {
    @Id
    @Column(name = "inode")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "文件主键", required = true, dataType = "Long")
    private Long inode;
    @Basic
    @Column(name = "filename")
    @ApiModelProperty(value = "文件名", dataType = "String")
    private String filename;
    @Basic
    @Column(name = "atime")
    @ApiModelProperty(value = "文件访问时间")
    private Timestamp atime;
    @Basic
    @Column(name = "ctime")
    @ApiModelProperty(value = "文件创建时间")
    private Timestamp ctime;
    @Basic
    @Column(name = "message_digest")
    @ApiModelProperty(value = "文件消息摘要")
    private String messageDigest;
    @Basic
    @Column(name = "mtime")
    @ApiModelProperty(value = "文件修改时间")
    private Timestamp mtime;
    @Basic
    @Column(name = "size")
    @ApiModelProperty(value = "文件大小")
    private Long size;
    @Basic
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "文件状态")
    private FileState state;
    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "文件类型")
    private FileType type;

    @Basic
    @Column(name = "file_key")
    @ApiModelProperty(value = "文件密钥")
    private String fileKey;

    @ApiModelProperty(value = "文件块数量")
    @Basic
    @Column(name = "block_size")
    private Integer blockSize;
    @ManyToOne
    @JoinColumn(name = "parent_inode", referencedColumnName = "inode", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty("父目录")
    private FilePo parentDir;
    @OneToMany(mappedBy = "parentDir",fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE})
    @ApiModelProperty(value = "子文件")
    @ToString.Exclude
    private Set<FilePo> childrenFiles;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @ApiModelProperty(value = "文件所属用户")
    private UserPo user;
    @OneToMany(mappedBy = "parentFilePo",fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE})
    @ApiModelProperty(value = "文件所有块")
    @ToString.Exclude
    private Set<FileBlockPo> fileBlocks;
    public FilePo(Long inode) {
        this.inode = inode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FilePo filePo = (FilePo) o;
        return inode != null && Objects.equals(inode, filePo.inode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
