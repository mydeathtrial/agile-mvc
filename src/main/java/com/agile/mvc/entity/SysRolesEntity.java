package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：[系统管理]角色
 *
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
    @Builder.Default
    private String parentId = "root";
    @Remark("角色名称")
    private String roleName;
    @Remark("角色说明")
    private String roleDesc;
    @Remark("是否可用")
    private Boolean enable;

    private List<SysRolesEntity> children = new ArrayList<>();

    @Transient
    public List<SysRolesEntity> getChildren() {
        return children;
    }

    @Column(name = "SYS_ROLES_ID", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysRolesId() {
        return sysRolesId;
    }

    @Column(name = "parent_id", columnDefinition = "VARCHAR default root", length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Column(name = "ROLE_NAME", length = 24)
    @Basic
    public String getRoleName() {
        return roleName;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Column(name = "ROLE_DESC", length = 100)
    @Basic
    public String getRoleDesc() {
        return roleDesc;
    }

    @Column(name = "ENABLE", columnDefinition = "BIT default 0", length = 1)
    @Basic
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
