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
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.persistence.Id;

/**
 * 描述：[系统管理]角色
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_roles")
@Remark("[系统管理]角色")
public class SysRolesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("角色唯一标识")
    private String sysRolesId;
    @Remark("角色名称")
    private String roleName;
    @Remark("角色说明")
    private String roleDesc;
    @Remark("是否可用")
    private Boolean enable;

    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Column(name = "SYS_ROLES_ID", nullable = false, length = 18)
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysRolesId() {
        return sysRolesId;
    }

    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Column(name = "ROLE_NAME", length = 24)
    @Basic
    public String getRoleName() {
        return roleName;
    }

    @Column(name = "ROLE_DESC", length = 200)
    @Length(max = 200, message = "最长为200个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getRoleDesc() {
        return roleDesc;
    }

    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    @Column(name = "ENABLE", length = 1)
    public Boolean getEnable() {
        return enable;
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
