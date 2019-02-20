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
import org.hibernate.annotations.GenerationTime;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Generated;
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
    @Remark("账号")
    private String saltKey;
    @Remark("密码")
    private String saltValue;
    @Remark("用户姓名")
    private String name;
    @Remark("所属机构ID")
    private String vQzjgid;
    @Remark("所属机构名称")
    private String vQzjgmc;
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

    @Column(name = "SYS_USERS_ID", nullable = false, length = 8)
    @Id
    public String getSysUsersId() {
        return sysUsersId;
    }

    @Column(name = "SALT_KEY", nullable = false, length = 24)
    @Basic
    public String getSaltKey() {
        return saltKey;
    }

    @Column(name = "SALT_VALUE", nullable = false, length = 100)
    @Basic
    public String getSaltValue() {
        return saltValue;
    }

    @Column(name = "NAME", length = 8)
    @Basic
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "V_QZJGID", length = 8)
    public String getVQzjgid() {
        return vQzjgid;
    }

    @Basic
    @Column(name = "V_QZJGMC", length = 24)
    public String getVQzjgmc() {
        return vQzjgmc;
    }

    @Column(name = "AREA_ID", length = 8)
    @Basic
    public String getAreaId() {
        return areaId;
    }

    @Column(name = "EXPIRED_TIME", length = 26)
    @Basic
    public Date getExpiredTime() {
        return expiredTime;
    }

    @Column(name = "IS_LOCKED", length = 1)
    @Basic
    public Boolean getIsLocked() {
        return isLocked;
    }

    @Column(name = "ON_LINE_STRATEGY", length = 4)
    @Basic
    public String getOnLineStrategy() {
        return onLineStrategy;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "CREATE_TIME", length = 26)
    public Date getCreateTime() {
        return createTime;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "UPDATE_TIME", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Basic
    @Column(name = "ENABLED", length = 1)
    public Boolean getEnabled() {
        return enabled;
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
