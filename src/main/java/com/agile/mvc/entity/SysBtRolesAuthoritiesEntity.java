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
 * 描述：[系统管理]角色权限表
 * @author agile gennerator
 */
@Entity
@Table(name = "sys_bt_roles_authorities")
@Remark("[系统管理]角色权限表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysBtRolesAuthoritiesEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtRolesAuthoritiesId;
    @Remark("权限唯一标识")
    private String authorityId;
    @Remark("角色唯一标识")
    private String roleId;

    /**
     * 无参构造器
     */
    public SysBtRolesAuthoritiesEntity() { }

    /**
     * 带参构造器
     */
    public SysBtRolesAuthoritiesEntity(String sysBtRolesAuthoritiesId, String authorityId, String roleId) {
        this.sysBtRolesAuthoritiesId = sysBtRolesAuthoritiesId;
        this.authorityId = authorityId;
        this.roleId = roleId;
    }

    @Id
    @Column(name = "sys_bt_roles_authorities_id", nullable = false)
    public String getSysBtRolesAuthoritiesId() {
        return sysBtRolesAuthoritiesId;
    }

    public void setSysBtRolesAuthoritiesId(String sysBtRolesAuthoritiesId) {
    this.sysBtRolesAuthoritiesId = sysBtRolesAuthoritiesId;
    }

    @Basic
    @Column(name = "authority_id", nullable = false)
    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
    this.authorityId = authorityId;
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
        if (!(object instanceof SysBtRolesAuthoritiesEntity)) {
            return false;
        }
        SysBtRolesAuthoritiesEntity that = (SysBtRolesAuthoritiesEntity) object;
        return Objects.equals(getSysBtRolesAuthoritiesId(), that.getSysBtRolesAuthoritiesId())
        && Objects.equals(getAuthorityId(), that.getAuthorityId())
        && Objects.equals(getRoleId(), that.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysBtRolesAuthoritiesId(), getAuthorityId(), getRoleId());
    }

    @Override
    public String toString() {
    return "SysBtRolesAuthoritiesEntity{"
            + "sysBtRolesAuthoritiesId ='" + sysBtRolesAuthoritiesId + '\''
            + ",authorityId ='" + authorityId + '\''
            + ",roleId ='" + roleId + '\'' + '}';
    }

    private SysBtRolesAuthoritiesEntity(Builder builder) {
        this.sysBtRolesAuthoritiesId = builder.sysBtRolesAuthoritiesId;
        this.authorityId = builder.authorityId;
        this.roleId = builder.roleId;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String sysBtRolesAuthoritiesId;
        private String authorityId;
        private String roleId;

        public Builder setSysBtRolesAuthoritiesId(String sysBtRolesAuthoritiesId) {
            this.sysBtRolesAuthoritiesId = sysBtRolesAuthoritiesId;
            return this;
        }
        public Builder setAuthorityId(String authorityId) {
            this.authorityId = authorityId;
            return this;
        }
        public Builder setRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }
        public SysBtRolesAuthoritiesEntity build() {
            return new SysBtRolesAuthoritiesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysBtRolesAuthoritiesEntity clone() {
        try {
            return (SysBtRolesAuthoritiesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
