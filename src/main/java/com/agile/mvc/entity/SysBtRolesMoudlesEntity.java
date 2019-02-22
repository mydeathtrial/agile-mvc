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
import javax.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import javax.persistence.Id;
import org.hibernate.validator.constraints.Length;
import org.apache.ibatis.annotations.Insert;

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

    @Column(name = "SYS_BT_ROLES_MOUDLES_ID", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysBtRolesMoudlesId() {
        return sysBtRolesMoudlesId;
    }

    @Column(name = "MODULE_ID", nullable = false, length = 18)
    @Basic
    @NotBlank(message = "模块唯一标识不能为空", groups = {Insert.class, Update.class})
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getModuleId() {
        return moduleId;
    }

    @Column(name = "ROLE_ID", nullable = false, length = 18)
    @NotBlank(message = "角色唯一标识不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
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
