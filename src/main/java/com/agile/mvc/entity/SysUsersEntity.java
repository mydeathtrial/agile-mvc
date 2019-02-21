package com.agile.mvc.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.TemporalType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.persistence.Id;

/**
 * 描述：[系统管理]用户
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
    private String onLineStrategy;
    @Remark("创建时间")
    private Date createTime;
    @Remark("修改时间")
    private Date updateTime;
    @Remark("是否可用")
    private Boolean enabled;
    @Remark("直属领导")
    private String leader;
    @Remark("账号使用开始日")
    private Long periodFrom;
    @Remark("账号使用结束日")
    private Long periodTo;
    @Remark("员工性别 0:男 1:女")
    private Boolean sex;
    @Remark("联系电话")
    private String telephone;
    @Remark("电子邮箱")
    private String email;

    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Column(name = "sys_users_id", nullable = false, length = 18)
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
    @Column(name = "salt_key", nullable = false, length = 24)
    @Basic
    @NotEmpty(message = "账号不能为空", groups = {Insert.class, Update.class})
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
    @NotEmpty(message = "密码不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "salt_value", nullable = false, length = 100)
    public String getSaltValue() {
        return saltValue;
    }

    @Column(name = "name", length = 8)
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "area_id", length = 8)
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getAreaId() {
        return areaId;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "expired_time", length = 26)
    public Date getExpiredTime() {
        return expiredTime;
    }

    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    @Column(name = "is_locked", length = 1)
    public Boolean getIsLocked() {
        return isLocked;
    }

    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    @Column(name = "on_line_strategy", length = 4)
    public String getOnLineStrategy() {
        return onLineStrategy;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time", length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @UpdateTimestamp
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Column(name = "enabled", length = 1)
    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    public Boolean getEnabled() {
        return enabled;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "leader", length = 100)
    public String getLeader() {
        return leader;
    }

    @Length(max = 19, message = "最长为19个字符", groups = {Insert.class, Update.class})
    @Column(name = "period_from", nullable = false, length = 19)
    @Basic
    @NotEmpty(message = "账号使用开始日不能为空", groups = {Insert.class, Update.class})
    public Long getPeriodFrom() {
        return periodFrom;
    }

    @Length(max = 19, message = "最长为19个字符", groups = {Insert.class, Update.class})
    @Column(name = "period_to", nullable = false, length = 19)
    @NotEmpty(message = "账号使用结束日不能为空", groups = {Insert.class, Update.class})
    @Basic
    public Long getPeriodTo() {
        return periodTo;
    }

    @Column(name = "sex", length = 1)
    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    public Boolean getSex() {
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
    @NotEmpty(message = "电子邮箱不能为空", groups = {Insert.class, Update.class})
    public String getEmail() {
        return email;
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
