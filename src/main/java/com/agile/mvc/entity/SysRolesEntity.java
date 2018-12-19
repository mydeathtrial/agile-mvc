package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "sys_roles", catalog = "agile_db")
@Remark("[系统管理]角色")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysRolesEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("角色唯一标识")
    private String sysRolesId;
    @Remark("角色名称")
    private String roleName;
    @Remark("角色说明")
    private String roleDesc;
    @Remark("是否可用")
    private Boolean enable;

    //无参构造器
    public SysRolesEntity() {
    }

    //有参构造器
    public SysRolesEntity(String sysRolesId, String roleName, String roleDesc, Boolean enable) {
        this.sysRolesId = sysRolesId;
        this.roleName = roleName;
        this.roleDesc = roleDesc;
        this.enable = enable;
    }

    @Id
    @Column(name = "sys_roles_id", nullable = false)
    public String getSysRolesId() {
        return sysRolesId;
    }

    public void setSysRolesId(String sysRolesId) {
        this.sysRolesId = sysRolesId;
    }

    @Basic
    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Basic
    @Column(name = "role_desc")
    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
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
        if (!(object instanceof SysRolesEntity)) return false;
        SysRolesEntity that = (SysRolesEntity) object;
        return Objects.equals(getSysRolesId(), that.getSysRolesId()) &&
                Objects.equals(getRoleName(), that.getRoleName()) &&
                Objects.equals(getRoleDesc(), that.getRoleDesc()) &&
                Objects.equals(getEnable(), that.getEnable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysRolesId(), getRoleName(), getRoleDesc(), getEnable());
    }

    @Override
    public String toString() {
        return "SysRolesEntity{" +
                "sysRolesId='" + sysRolesId + '\'' +
                ",roleName='" + roleName + '\'' +
                ",roleDesc='" + roleDesc + '\'' +
                ",enable=" + enable +
                '}';
    }

    private SysRolesEntity(Builder builder) {
        this.sysRolesId = builder.sysRolesId;
        this.roleName = builder.roleName;
        this.roleDesc = builder.roleDesc;
        this.enable = builder.enable;
    }

    public static class Builder {
        private String sysRolesId;
        private String roleName;
        private String roleDesc;
        private Boolean enable;

        public Builder setSysRolesId(String sysRolesId) {
            this.sysRolesId = sysRolesId;
            return this;
        }

        public Builder setRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder setRoleDesc(String roleDesc) {
            this.roleDesc = roleDesc;
            return this;
        }

        public Builder setEnable(Boolean enable) {
            this.enable = enable;
            return this;
        }

        public SysRolesEntity build() {
            return new SysRolesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysRolesEntity clone() {
        try {
            return (SysRolesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }
}
