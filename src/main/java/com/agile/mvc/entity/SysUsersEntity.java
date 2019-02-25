package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：[系统管理]用户
 *
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_users")
@Remark("[系统管理]用户")
public class SysUsersEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysUsersId;
    @Remark("部门主键")
    private String sysDepartId;
    @Remark("账号")
    private String saltKey;
    @Remark("旧密码")
    private String saltValueOld;
    @Remark("密码")
    private String saltValue;
    @Remark("用户姓名")
    private String name;
    @Remark("地区编号")
    private String areaId;
    @Remark("过期时间")
    private Date expiredTime;
    @Remark("用户是否锁定")
    private Boolean isLocked;
    @Remark("同时在线策略")
    @Builder.Default
    private String onLineStrategy = "1000";
    @Remark("是否可用")
    private Boolean enabled;
    @Remark("直属领导")
    private String leader;
    @Remark("员工性别 0:男 1:女")
    private Integer sex;
    @Remark("联系电话")
    private String telephone;
    @Remark("电子邮箱")
    private String email;
    @Remark("创建时间")
    private Date createTime;
    @Remark("修改时间")
    private Date updateTime;

    @Column(name = "sys_users_id", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysUsersId() {
        return sysUsersId;
    }

    @Column(name = "sys_depart_id", length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysDepartId() {
        return sysDepartId;
    }

    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @NotBlank(message = "账号不能为空", groups = {Insert.class, Update.class})
    @Column(name = "salt_key", nullable = false, length = 24)
    @Basic
    public String getSaltKey() {
        return saltKey;
    }

    @Length(max = 32, message = "最长为32个字符", groups = {Insert.class, Update.class})
    @Column(name = "salt_value_old", length = 32)
    @Basic
    public String getSaltValueOld() {
        return saltValueOld;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @NotBlank(message = "密码不能为空", groups = {Insert.class, Update.class})
    @Column(name = "salt_value", nullable = false, length = 100)
    public String getSaltValue() {
        return saltValue;
    }

    @NotBlank(message = "用户姓名不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    @Column(name = "name", nullable = false, length = 8)
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "area_id", length = 8)
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getAreaId() {
        return areaId;
    }

    @Basic
    @Column(name = "expired_time", length = 26)
    public Date getExpiredTime() {
        return expiredTime;
    }

    @Basic
    @Column(name = "is_locked", columnDefinition = "BIT default 0", length = 1)
    public Boolean getIsLocked() {
        return isLocked;
    }

    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    @Column(name = "on_line_strategy", columnDefinition = "VARCHAR default 1000", length = 4)
    public String getOnLineStrategy() {
        return onLineStrategy;
    }

    @Basic
    @Column(name = "enabled", columnDefinition = "BIT default 0", length = 1)
    public Boolean getEnabled() {
        return enabled;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "leader", length = 100)
    public String getLeader() {
        return leader;
    }

    @Column(name = "sex", columnDefinition = "BIT default 0", length = 1)
    @Basic
    public Integer getSex() {
        return sex;
    }

    @Column(name = "telephone", length = 11)
    @Basic
    @Length(max = 11, message = "最长为11个字符", groups = {Insert.class, Update.class})
    public String getTelephone() {
        return telephone;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Column(name = "email", nullable = false, length = 100)
    @Basic
    @NotBlank(message = "电子邮箱不能为空", groups = {Insert.class, Update.class})
    public String getEmail() {
        return email;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Past
    @CreationTimestamp
    @Column(name = "create_time", length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    @UpdateTimestamp
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public SysUsersEntity clone() {
        try {
            return (SysUsersEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
