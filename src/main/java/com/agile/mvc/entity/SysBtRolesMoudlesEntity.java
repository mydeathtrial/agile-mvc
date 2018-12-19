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
@Table(name = "sys_bt_roles_moudles", catalog = "agile_db")
@Remark("[系统管理]角色模块表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysBtRolesMoudlesEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtRolesMoudlesId;
    @Remark("模块唯一标识")
    private String moduleId;
    @Remark("角色唯一标识")
    private String roleId;

    //无参构造器
    public SysBtRolesMoudlesEntity() {
    }

    //有参构造器
    public SysBtRolesMoudlesEntity(String sysBtRolesMoudlesId, String moduleId, String roleId) {
        this.sysBtRolesMoudlesId = sysBtRolesMoudlesId;
        this.moduleId = moduleId;
        this.roleId = roleId;
    }

    private SysBtRolesMoudlesEntity(Builder builder) {
        this.sysBtRolesMoudlesId = builder.sysBtRolesMoudlesId;
        this.moduleId = builder.moduleId;
        this.roleId = builder.roleId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Id
    @Column(name = "sys_bt_roles_moudles_id", nullable = false)
    public String getSysBtRolesMoudlesId() {
        return sysBtRolesMoudlesId;
    }

    public void setSysBtRolesMoudlesId(String sysBtRolesMoudlesId) {
        this.sysBtRolesMoudlesId = sysBtRolesMoudlesId;
    }

    @Basic
    @Column(name = "module_id", nullable = false)
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Basic
    @Column(name = "role_id", nullable = false)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysBtRolesMoudlesEntity)) {
            return false;
        }
        SysBtRolesMoudlesEntity that = (SysBtRolesMoudlesEntity) object;
        return Objects.equals(getSysBtRolesMoudlesId(), that.getSysBtRolesMoudlesId()) &&
                Objects.equals(getModuleId(), that.getModuleId()) &&
                Objects.equals(getRoleId(), that.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysBtRolesMoudlesId(), getModuleId(), getRoleId());
    }

    @Override
    public String toString() {
        return "SysBtRolesMoudlesEntity{" +
                "sysBtRolesMoudlesId='" + sysBtRolesMoudlesId + '\'' +
                ",moduleId='" + moduleId + '\'' +
                ",roleId='" + roleId + '\'' +
                '}';
    }

    @Override
    public SysBtRolesMoudlesEntity clone() {
        try {
            return (SysBtRolesMoudlesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }

    public static class Builder {
        private String sysBtRolesMoudlesId;
        private String moduleId;
        private String roleId;

        public Builder setSysBtRolesMoudlesId(String sysBtRolesMoudlesId) {
            this.sysBtRolesMoudlesId = sysBtRolesMoudlesId;
            return this;
        }

        public Builder setModuleId(String moduleId) {
            this.moduleId = moduleId;
            return this;
        }

        public Builder setRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        public SysBtRolesMoudlesEntity build() {
            return new SysBtRolesMoudlesEntity(this);
        }
    }
}
