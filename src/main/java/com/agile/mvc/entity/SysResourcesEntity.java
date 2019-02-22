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
 * 描述：[系统管理]资源
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_resources")
@Remark("[系统管理]资源")
public class SysResourcesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysResourcesId;
    @Remark("资源类型")
    private String type;
    @Remark("资源名称")
    private String name;
    @Remark("资源描述")
    private String desc;
    @Remark("资源路径")
    private String path;
    @Remark("优先级")
    private String priority;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("是否系统权限")
    private Boolean issys;
    @Remark("模块")
    private String moduleId;

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    @Column(name = "SYS_RESOURCES_ID", nullable = false, length = 18)
    public String getSysResourcesId() {
        return sysResourcesId;
    }

    @Column(name = "TYPE", length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getType() {
        return type;
    }

    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Column(name = "NAME", length = 24)
    @Basic
    public String getName() {
        return name;
    }

    @Length(max = 200, message = "最长为200个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "DESC", length = 200)
    public String getDesc() {
        return desc;
    }

    @Length(max = 200, message = "最长为200个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "PATH", length = 200)
    public String getPath() {
        return path;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Column(name = "PRIORITY", length = 100)
    @Basic
    public String getPriority() {
        return priority;
    }

    @Basic
    @Column(name = "ENABLE", length = 1)
    public Boolean getEnable() {
        return enable;
    }

    @Column(name = "ISSYS", length = 1)
    @Basic
    public Boolean getIssys() {
        return issys;
    }

    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    @Column(name = "MODULE_ID", length = 8)
    public String getModuleId() {
        return moduleId;
    }


    @Override
    public SysResourcesEntity clone() {
        try {
            return (SysResourcesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
