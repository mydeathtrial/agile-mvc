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
 * 描述：[系统管理]角色模块表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_bt_roles_moudles")
@Remark("[系统管理]角色模块表")
public class SysBtRolesMoudlesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtRolesMoudlesId;
    @Remark("模块唯一标识")
    private String moduleId;
    @Remark("角色唯一标识")
    private String roleId;

    @Column(name = "SYS_BT_ROLES_MOUDLES_ID", nullable = false, length = 8)
    @Id
    public String getSysBtRolesMoudlesId() {
        return sysBtRolesMoudlesId;
    }

    @Basic
    @Column(name = "MODULE_ID", nullable = false, length = 8)
    public String getModuleId() {
        return moduleId;
    }

    @Column(name = "ROLE_ID", nullable = false, length = 8)
    @Basic
    public String getRoleId() {
        return roleId;
    }


    @Override
    public SysBtRolesMoudlesEntity clone() {
        try {
            return (SysBtRolesMoudlesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
