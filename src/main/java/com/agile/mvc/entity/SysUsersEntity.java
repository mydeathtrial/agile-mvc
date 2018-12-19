package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "sys_users", catalog = "agile_db")
@Remark("[系统管理]用户")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysUsersEntity implements Serializable, Cloneable {

    //序列
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

    //无参构造器
    public SysUsersEntity() {
    }

    //有参构造器
    public SysUsersEntity(String sysUsersId, String saltKey, String saltValue, String name, String vQzjgid, String vQzjgmc, String areaId, Date expiredTime, Boolean isLocked, String onLineStrategy, Date createTime, Date updateTime, Boolean enabled) {
        this.sysUsersId = sysUsersId;
        this.saltKey = saltKey;
        this.saltValue = saltValue;
        this.name = name;
        this.vQzjgid = vQzjgid;
        this.vQzjgmc = vQzjgmc;
        this.areaId = areaId;
        this.expiredTime = expiredTime;
        this.isLocked = isLocked;
        this.onLineStrategy = onLineStrategy;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.enabled = enabled;
    }

    @Id
    @Column(name = "sys_users_id", nullable = false)
    public String getSysUsersId() {
        return sysUsersId;
    }

    public void setSysUsersId(String sysUsersId) {
        this.sysUsersId = sysUsersId;
    }

    @Basic
    @Column(name = "salt_key", nullable = false)
    public String getSaltKey() {
        return saltKey;
    }

    public void setSaltKey(String saltKey) {
        this.saltKey = saltKey;
    }

    @Basic
    @Column(name = "salt_value", nullable = false)
    public String getSaltValue() {
        return saltValue;
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "v_qzjgid")
    public String getVQzjgid() {
        return vQzjgid;
    }

    public void setVQzjgid(String vQzjgid) {
        this.vQzjgid = vQzjgid;
    }

    @Basic
    @Column(name = "v_qzjgmc")
    public String getVQzjgmc() {
        return vQzjgmc;
    }

    public void setVQzjgmc(String vQzjgmc) {
        this.vQzjgmc = vQzjgmc;
    }

    @Basic
    @Column(name = "area_id")
    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    @Basic
    @Column(name = "expired_time")
    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Basic
    @Column(name = "is_locked")
    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Basic
    @Column(name = "on_line_strategy")
    public String getOnLineStrategy() {
        return onLineStrategy;
    }

    public void setOnLineStrategy(String onLineStrategy) {
        this.onLineStrategy = onLineStrategy;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SysUsersEntity)) return false;
        SysUsersEntity that = (SysUsersEntity) object;
        return Objects.equals(getSysUsersId(), that.getSysUsersId()) &&
                Objects.equals(getSaltKey(), that.getSaltKey()) &&
                Objects.equals(getSaltValue(), that.getSaltValue()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getVQzjgid(), that.getVQzjgid()) &&
                Objects.equals(getVQzjgmc(), that.getVQzjgmc()) &&
                Objects.equals(getAreaId(), that.getAreaId()) &&
                Objects.equals(getExpiredTime(), that.getExpiredTime()) &&
                Objects.equals(getIsLocked(), that.getIsLocked()) &&
                Objects.equals(getOnLineStrategy(), that.getOnLineStrategy()) &&
                Objects.equals(getCreateTime(), that.getCreateTime()) &&
                Objects.equals(getUpdateTime(), that.getUpdateTime()) &&
                Objects.equals(getEnabled(), that.getEnabled());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysUsersId(), getSaltKey(), getSaltValue(), getName(), getVQzjgid(), getVQzjgmc(), getAreaId(), getExpiredTime(), getIsLocked(), getOnLineStrategy(), getCreateTime(), getUpdateTime(), getEnabled());
    }

    @Override
    public String toString() {
        return "SysUsersEntity{" +
                "sysUsersId='" + sysUsersId + '\'' +
                ",saltKey='" + saltKey + '\'' +
                ",saltValue='" + saltValue + '\'' +
                ",name='" + name + '\'' +
                ",vQzjgid='" + vQzjgid + '\'' +
                ",vQzjgmc='" + vQzjgmc + '\'' +
                ",areaId='" + areaId + '\'' +
                ",expiredTime=" + expiredTime +
                ",isLocked=" + isLocked +
                ",onLineStrategy='" + onLineStrategy + '\'' +
                ",createTime=" + createTime +
                ",updateTime=" + updateTime +
                ",enabled=" + enabled +
                '}';
    }

    private SysUsersEntity(Builder builder) {
        this.sysUsersId = builder.sysUsersId;
        this.saltKey = builder.saltKey;
        this.saltValue = builder.saltValue;
        this.name = builder.name;
        this.vQzjgid = builder.vQzjgid;
        this.vQzjgmc = builder.vQzjgmc;
        this.areaId = builder.areaId;
        this.expiredTime = builder.expiredTime;
        this.isLocked = builder.isLocked;
        this.onLineStrategy = builder.onLineStrategy;
        this.createTime = builder.createTime;
        this.updateTime = builder.updateTime;
        this.enabled = builder.enabled;
    }

    public static class Builder {
        private String sysUsersId;
        private String saltKey;
        private String saltValue;
        private String name;
        private String vQzjgid;
        private String vQzjgmc;
        private String areaId;
        private Date expiredTime;
        private Boolean isLocked;
        private String onLineStrategy;
        private Date createTime;
        private Date updateTime;
        private Boolean enabled;

        public Builder setSysUsersId(String sysUsersId) {
            this.sysUsersId = sysUsersId;
            return this;
        }

        public Builder setSaltKey(String saltKey) {
            this.saltKey = saltKey;
            return this;
        }

        public Builder setSaltValue(String saltValue) {
            this.saltValue = saltValue;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setVQzjgid(String vQzjgid) {
            this.vQzjgid = vQzjgid;
            return this;
        }

        public Builder setVQzjgmc(String vQzjgmc) {
            this.vQzjgmc = vQzjgmc;
            return this;
        }

        public Builder setAreaId(String areaId) {
            this.areaId = areaId;
            return this;
        }

        public Builder setExpiredTime(Date expiredTime) {
            this.expiredTime = expiredTime;
            return this;
        }

        public Builder setIsLocked(Boolean isLocked) {
            this.isLocked = isLocked;
            return this;
        }

        public Builder setOnLineStrategy(String onLineStrategy) {
            this.onLineStrategy = onLineStrategy;
            return this;
        }

        public Builder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public SysUsersEntity build() {
            return new SysUsersEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
