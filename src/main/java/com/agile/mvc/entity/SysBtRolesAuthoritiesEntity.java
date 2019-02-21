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
 * 描述：[系统管理]角色权限表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_bt_roles_authorities")
@Remark("[系统管理]角色权限表")
public class SysBtRolesAuthoritiesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtRolesAuthoritiesId;
    @Remark("权限唯一标识")
    private String authorityId;
    @Remark("角色唯一标识")
    private String roleId;

    @Column(name = "SYS_BT_ROLES_AUTHORITIES_ID", nullable = false, length = 18)
    @Id
    public String getSysBtRolesAuthoritiesId() {
        return sysBtRolesAuthoritiesId;
    }

    @Basic
    @Column(name = "AUTHORITY_ID", nullable = false, length = 18)
    public String getAuthorityId() {
        return authorityId;
    }

    @Column(name = "ROLE_ID", nullable = false, length = 18)
    @Basic
    public String getRoleId() {
        return roleId;
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
