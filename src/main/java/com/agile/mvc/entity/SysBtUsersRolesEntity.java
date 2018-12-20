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
 * 描述：[系统管理]用户角色表
 * @author agile gennerator
 */
@Entity
@Table(name = "sys_bt_users_roles")
@Remark("[系统管理]用户角色表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysBtUsersRolesEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtUsersRolesId;
    @Remark("角色唯一标识")
    private String roleId;
    @Remark("用户唯一标识")
    private String userId;

    /**
     * 无参构造器
     */
    public SysBtUsersRolesEntity() { }

    /**
     * 带参构造器
     */
    public SysBtUsersRolesEntity(String sysBtUsersRolesId, String roleId, String userId) {
        this.sysBtUsersRolesId = sysBtUsersRolesId;
        this.roleId = roleId;
        this.userId = userId;
    }

    @Id
    @Column(name = "sys_bt_users_roles_id", nullable = false)
    public String getSysBtUsersRolesId() {
        return sysBtUsersRolesId;
    }

    public void setSysBtUsersRolesId(String sysBtUsersRolesId) {
    this.sysBtUsersRolesId = sysBtUsersRolesId;
    }

    @Basic
    @Column(name = "role_id", nullable = false)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
    this.roleId = roleId;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
    this.userId = userId;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysBtUsersRolesEntity)) {
            return false;
        }
        SysBtUsersRolesEntity that = (SysBtUsersRolesEntity) object;
        return Objects.equals(getSysBtUsersRolesId(), that.getSysBtUsersRolesId())
        && Objects.equals(getRoleId(), that.getRoleId())
        && Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysBtUsersRolesId(), getRoleId(), getUserId());
    }

    @Override
    public String toString() {
    return "SysBtUsersRolesEntity{"
            + "sysBtUsersRolesId ='" + sysBtUsersRolesId + '\''
            + ",roleId ='" + roleId + '\''
            + ",userId ='" + userId + '\'' + '}';
    }

    private SysBtUsersRolesEntity(Builder builder) {
        this.sysBtUsersRolesId = builder.sysBtUsersRolesId;
        this.roleId = builder.roleId;
        this.userId = builder.userId;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String sysBtUsersRolesId;
        private String roleId;
        private String userId;

        public Builder setSysBtUsersRolesId(String sysBtUsersRolesId) {
            this.sysBtUsersRolesId = sysBtUsersRolesId;
            return this;
        }
        public Builder setRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }
        public SysBtUsersRolesEntity build() {
            return new SysBtUsersRolesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysBtUsersRolesEntity clone() {
        try {
            return (SysBtUsersRolesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
