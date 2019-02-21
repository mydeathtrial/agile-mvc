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
import javax.persistence.Id;

/**
 * 描述：[系统管理]用户角色表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_bt_users_roles")
@Remark("[系统管理]用户角色表")
public class SysBtUsersRolesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtUsersRolesId;
    @Remark("角色唯一标识")
    private String roleId;
    @Remark("用户唯一标识")
    private String userId;

    @Column(name = "SYS_BT_USERS_ROLES_ID", nullable = false, length = 18)
    @Id
    public String getSysBtUsersRolesId() {
        return sysBtUsersRolesId;
    }

    @Column(name = "ROLE_ID", nullable = false, length = 18)
    @Basic
    public String getRoleId() {
        return roleId;
    }

    @Column(name = "USER_ID", nullable = false, length = 18)
    @Basic
    public String getUserId() {
        return userId;
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
