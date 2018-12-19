package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "sys_authorities", catalog = "agile_db")
@Remark("[系统管理]权限")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysAuthoritiesEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysAuthorityId;
    @Remark("权限标识")
    private String mark;
    @Remark("权限名称")
    private String name;
    @Remark("权限说明")
    private String desc;
    @Remark("是否可用")
    private Boolean enable;

    //无参构造器
    public SysAuthoritiesEntity() {
    }

    //有参构造器
    public SysAuthoritiesEntity(String sysAuthorityId, String mark, String name, String desc, Boolean enable) {
        this.sysAuthorityId = sysAuthorityId;
        this.mark = mark;
        this.name = name;
        this.desc = desc;
        this.enable = enable;
    }

    @Id
    @Column(name = "sys_authority_id", nullable = false)
    public String getSysAuthorityId() {
        return sysAuthorityId;
    }

    public void setSysAuthorityId(String sysAuthorityId) {
        this.sysAuthorityId = sysAuthorityId;
    }

    @Basic
    @Column(name = "mark")
    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "desc")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Basic
    @Column(name = "enable")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SysAuthoritiesEntity)) return false;
        SysAuthoritiesEntity that = (SysAuthoritiesEntity) object;
        return Objects.equals(getSysAuthorityId(), that.getSysAuthorityId()) &&
                Objects.equals(getMark(), that.getMark()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDesc(), that.getDesc()) &&
                Objects.equals(getEnable(), that.getEnable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysAuthorityId(), getMark(), getName(), getDesc(), getEnable());
    }

    @Override
    public String toString() {
        return "SysAuthoritiesEntity{" +
                "sysAuthorityId='" + sysAuthorityId + '\'' +
                ",mark='" + mark + '\'' +
                ",name='" + name + '\'' +
                ",desc='" + desc + '\'' +
                ",enable=" + enable +
                '}';
    }

    private SysAuthoritiesEntity(Builder builder) {
        this.sysAuthorityId = builder.sysAuthorityId;
        this.mark = builder.mark;
        this.name = builder.name;
        this.desc = builder.desc;
        this.enable = builder.enable;
    }

    public static class Builder {
        private String sysAuthorityId;
        private String mark;
        private String name;
        private String desc;
        private Boolean enable;

        public Builder setSysAuthorityId(String sysAuthorityId) {
            this.sysAuthorityId = sysAuthorityId;
            return this;
        }

        public Builder setMark(String mark) {
            this.mark = mark;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder setEnable(Boolean enable) {
            this.enable = enable;
            return this;
        }

        public SysAuthoritiesEntity build() {
            return new SysAuthoritiesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysAuthoritiesEntity clone() {
        try {
            return (SysAuthoritiesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }
}
