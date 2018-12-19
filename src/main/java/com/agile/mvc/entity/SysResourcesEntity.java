package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 描述：[系统管理]资源
 * @author agile gennerator
 */
@Entity
@Table(name = "sys_resources")
@Remark("[系统管理]资源")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysResourcesEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysResourcesId;
    @Remark("资源类型")
    private String type;
    @Remark("资源名称")
    private String name;
    @Remark("资源描述")
    private String desc;
    @Remark("资源路径")
    private String path;
    @Remark("优先级")
    private String priority;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("是否系统权限")
    private Boolean issys;
    @Remark("模块")
    private String moduleId;

    /**
     * 无参构造器
     */
    public SysResourcesEntity() { }

    /**
     * 带参构造器
     */
    public SysResourcesEntity(String sysResourcesId, String type, String name, String desc, String path, String priority, Boolean enable, Boolean issys, String moduleId) {
        this.sysResourcesId = sysResourcesId;
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.path = path;
        this.priority = priority;
        this.enable = enable;
        this.issys = issys;
        this.moduleId = moduleId;
    }

    @Id
    @Column(name = "sys_resources_id", nullable = false)
    public String getSysResourcesId() {
        return sysResourcesId;
    }

    public void setSysResourcesId(String sysResourcesId) {
    this.sysResourcesId = sysResourcesId;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
    this.type = type;
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
    @Column(name = "desc")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
    this.desc = desc;
    }

    @Basic
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
    this.path = path;
    }

    @Basic
    @Column(name = "priority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
    this.priority = priority;
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
    @Column(name = "issys")
    public Boolean getIssys() {
        return issys;
    }

    public void setIssys(Boolean issys) {
    this.issys = issys;
    }

    @Basic
    @Column(name = "module_id")
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysResourcesEntity)) {
            return false;
        }
        SysResourcesEntity that = (SysResourcesEntity) object;
        return Objects.equals(getSysResourcesId(), that.getSysResourcesId())
        && Objects.equals(getType(), that.getType())
        && Objects.equals(getName(), that.getName())
        && Objects.equals(getDesc(), that.getDesc())
        && Objects.equals(getPath(), that.getPath())
        && Objects.equals(getPriority(), that.getPriority())
        && Objects.equals(getEnable(), that.getEnable())
        && Objects.equals(getIssys(), that.getIssys())
        && Objects.equals(getModuleId(), that.getModuleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysResourcesId(), getType(), getName(), getDesc(), getPath(), getPriority(), getEnable(), getIssys(), getModuleId());
    }

    @Override
    public String toString() {
    return "SysResourcesEntity{"
            + "sysResourcesId='" + sysResourcesId + '\''
            + ",type='" + type + '\''
            + ",name='" + name + '\''
            + ",desc='" + desc + '\''
            + ",path='" + path + '\''
            + ",priority='" + priority + '\''
            + ",enable=" + enable
            + ",issys=" + issys
            + ",moduleId='" + moduleId + '\'' + '}';
    }

    private SysResourcesEntity(Builder builder) {
        this.sysResourcesId = builder.sysResourcesId;
        this.type = builder.type;
        this.name = builder.name;
        this.desc = builder.desc;
        this.path = builder.path;
        this.priority = builder.priority;
        this.enable = builder.enable;
        this.issys = builder.issys;
        this.moduleId = builder.moduleId;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String sysResourcesId;
        private String type;
        private String name;
        private String desc;
        private String path;
        private String priority;
        private Boolean enable;
        private Boolean issys;
        private String moduleId;

        public Builder setSysResourcesId(String sysResourcesId) {
            this.sysResourcesId = sysResourcesId;
            return this;
        }
        public Builder setType(String type) {
            this.type = type;
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
        public Builder setPath(String path) {
            this.path = path;
            return this;
        }
        public Builder setPriority(String priority) {
            this.priority = priority;
            return this;
        }
        public Builder setEnable(Boolean enable) {
            this.enable = enable;
            return this;
        }
        public Builder setIssys(Boolean issys) {
            this.issys = issys;
            return this;
        }
        public Builder setModuleId(String moduleId) {
            this.moduleId = moduleId;
            return this;
        }
        public SysResourcesEntity build() {
            return new SysResourcesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysResourcesEntity clone() {
        try {
            return (SysResourcesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
