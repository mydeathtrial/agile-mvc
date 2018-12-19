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
@Table(name = "sys_modules", catalog = "agile_db")
@Remark("[系统管理]模块")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysModulesEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysModulesId;
    @Remark("模块名称")
    private String name;
    @Remark("模块说明")
    private String desc;
    @Remark("模块上级")
    private String parentId;
    @Remark("模块地址")
    private String url;
    @Remark("级别")
    private String level;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("优先级")
    private Integer order;

    //无参构造器
    public SysModulesEntity() {
    }

    //有参构造器
    public SysModulesEntity(String sysModulesId, String name, String desc, String parentId, String url, String level, Boolean enable, Integer order) {
        this.sysModulesId = sysModulesId;
        this.name = name;
        this.desc = desc;
        this.parentId = parentId;
        this.url = url;
        this.level = level;
        this.enable = enable;
        this.order = order;
    }

    @Id
    @Column(name = "sys_modules_id", nullable = false)
    public String getSysModulesId() {
        return sysModulesId;
    }

    public void setSysModulesId(String sysModulesId) {
        this.sysModulesId = sysModulesId;
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
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "level")
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Basic
    @Column(name = "enable")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Basic
    @Column(name = "order")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysModulesEntity)) {
            return false;
        }
        SysModulesEntity that = (SysModulesEntity) object;
        return Objects.equals(getSysModulesId(), that.getSysModulesId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDesc(), that.getDesc()) &&
                Objects.equals(getParentId(), that.getParentId()) &&
                Objects.equals(getUrl(), that.getUrl()) &&
                Objects.equals(getLevel(), that.getLevel()) &&
                Objects.equals(getEnable(), that.getEnable()) &&
                Objects.equals(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysModulesId(), getName(), getDesc(), getParentId(), getUrl(), getLevel(), getEnable(), getOrder());
    }

    @Override
    public String toString() {
        return "SysModulesEntity{" +
                "sysModulesId='" + sysModulesId + '\'' +
                ",name='" + name + '\'' +
                ",desc='" + desc + '\'' +
                ",parentId='" + parentId + '\'' +
                ",url='" + url + '\'' +
                ",level='" + level + '\'' +
                ",enable=" + enable +
                ",order=" + order +
                '}';
    }

    private SysModulesEntity(Builder builder) {
        this.sysModulesId = builder.sysModulesId;
        this.name = builder.name;
        this.desc = builder.desc;
        this.parentId = builder.parentId;
        this.url = builder.url;
        this.level = builder.level;
        this.enable = builder.enable;
        this.order = builder.order;
    }

    public static class Builder {
        private String sysModulesId;
        private String name;
        private String desc;
        private String parentId;
        private String url;
        private String level;
        private Boolean enable;
        private Integer order;

        public Builder setSysModulesId(String sysModulesId) {
            this.sysModulesId = sysModulesId;
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

        public Builder setParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setLevel(String level) {
            this.level = level;
            return this;
        }

        public Builder setEnable(Boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder setOrder(Integer order) {
            this.order = order;
            return this;
        }

        public SysModulesEntity build() {
            return new SysModulesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysModulesEntity clone() {
        try {
            return (SysModulesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }
}
