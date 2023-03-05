package com.cloud.encrypting_cloud_storage.models.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2023/2/25
 * @version: 1.0
 */
@Entity
@Table(name = "share_file", schema = "cloud")
@ApiModel(value = "分享文件实体", description = "记录分享文件信息")
@Getter
@Setter
//@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ShareFilePo {
    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "分享文件主键")
    private long id;

    @OneToOne
    @JoinColumn(name = "file_id",referencedColumnName = "inode")
    @ApiModelProperty(value = "分享文件")
    private FilePo filePo;


    @OneToOne
    @JoinColumn(name = "sharer_user_id",referencedColumnName = "id")
    @ApiModelProperty(value = "分享者")
    private UserPo sharerUser;


    @OneToOne
    @JoinColumn(name = "target_user_id",referencedColumnName = "id")
    @ApiModelProperty(value = "被分享者")
    private UserPo targetUser;

    @Column(name = "share_time")
    @ApiModelProperty(value = "分享时间")
    private Date shareTime;

    public FilePo getFilePo() {
        return filePo;
    }

    public void setFilePo(FilePo filePo) {
        this.filePo = filePo;
    }

    public UserPo getSharerUser() {
        return sharerUser;
    }

    public void setSharerUser(UserPo sharerUser) {
        this.sharerUser = sharerUser;
    }

    public UserPo getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserPo targetUser) {
        this.targetUser = targetUser;
    }




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
