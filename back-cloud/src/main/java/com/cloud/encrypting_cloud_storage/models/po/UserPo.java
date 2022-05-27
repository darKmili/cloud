package com.cloud.encrypting_cloud_storage.models.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
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
@Table(name = "user", schema = "cloud")
@ApiModel(value = "用户持久层对象",description = "用户")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "userpo.all",attributeNodes = {@NamedAttributeNode(value = "files")})
@JsonIgnoreProperties(value = {"files","sha256VerifyKey"})
public class UserPo implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id",required = true)
    private Long id;
    @Basic
    @Column(name = "email")
    @Email
    @ApiModelProperty(value = "用户邮箱",required = true)
    private String email;
    @Basic
    @Column(name = "name")
    @ApiModelProperty(value = "用户名")
    private String name;
    @Basic
    @Column(name = "face")
    @ApiModelProperty(value = "用户头像")
    private byte[] face;
    @Basic
    @Column(name = "client_random_value")
    @ApiModelProperty(value = "客户端随机数",required = true)
    @NotNull
    private String clientRandomValue;
    @Basic
    @Column(name = "sha256verify_key")
    @ApiModelProperty(value = "256位验证哈希",required = true)
    @NotNull
    private String verifyKey;
    @Basic
    @Column(name = "encrypted_master_key")
    @ApiModelProperty(value = "已加密的主密钥",required = true)
    @NotNull
    private String encryptedMasterKey;
    @Basic
    @Column(name = "cur_load_time")
    @ApiModelProperty(value = "当前登录时间")
    private Timestamp curLoadTime;
    @Basic
    @Column(name = "last_load_time")
    @ApiModelProperty(value = "上传登录时间")
    private Timestamp lastLoadTime;
    @Basic
    @Column(name = "register_time")
    @ApiModelProperty(value = "注册时间")
    private Timestamp registerTime;
    @Basic
    @Column(name = "used_capacity")
    @ApiModelProperty(value = "用户已使用容量")
    private Long usedCapacity;
    @Basic
    @Column(name = "total_capacity")
    @ApiModelProperty(value = "用户预分配空间")
    private Long totalCapacity;
    @OneToMany(mappedBy = "user",cascade = {CascadeType.REMOVE},fetch = FetchType.LAZY)
    @ApiModelProperty(value = "用户的文件")
    @ToString.Exclude
    private Set<FilePo> files;

    /**
     * token不保存到数据库
     */
    @Transient
    @JsonProperty
    private String token;

    public UserPo(Long id){
        this.id=id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserPo userPo = (UserPo) o;
        return id != null && Objects.equals(id, userPo.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
