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
    private String saltKeyOld;
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

    @Column(name = "sys_users_id", nullable = false, length = 18)
    @Id
    public String getSysUsersId() {
        return sysUsersId;
    }

    @Column(name = "sys_depart_id", length = 18)
    @Basic
    public String getSysDepartId() {
        return sysDepartId;
    }

    @Column(name = "salt_key", nullable = false, length = 24)
    @Basic
    public String getSaltKey() {
        return saltKey;
    }

    @Column(name = "salt_key_old", length = 32)
    @Basic
    public String getSaltKeyOld() {
        return saltKeyOld;
    }

    @Basic
    @Column(name = "salt_value", nullable = false, length = 100)
    public String getSaltValue() {
        return saltValue;
    }

    @Column(name = "name", length = 8)
    @Basic
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "area_id", length = 8)
    public String getAreaId() {
        return areaId;
    }

    @Basic
    @Column(name = "expired_time", length = 26)
    public Date getExpiredTime() {
        return expiredTime;
    }

    @Basic
    @Column(name = "is_locked", length = 1)
    public Boolean getIsLocked() {
        return isLocked;
    }

    @Basic
    @Column(name = "on_line_strategy", length = 4)
    public String getOnLineStrategy() {
        return onLineStrategy;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "enabled", length = 1)
    @Basic
    public Boolean getEnabled() {
        return enabled;
    }

    @Basic
    @Column(name = "leader", length = 100)
    public String getLeader() {
        return leader;
    }

    @Column(name = "period_from", nullable = false, length = 19)
    @Basic
    public Long getPeriodFrom() {
        return periodFrom;
    }

    @Column(name = "period_to", nullable = false, length = 19)
    @Basic
    public Long getPeriodTo() {
        return periodTo;
    }

    @Column(name = "sex", length = 1)
    @Basic
    public Boolean getSex() {
        return sex;
    }

    @Column(name = "telephone", length = 11)
    @Basic
    public String getTelephone() {
        return telephone;
    }

    @Column(name = "email", nullable = false, length = 100)
    @Basic
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
