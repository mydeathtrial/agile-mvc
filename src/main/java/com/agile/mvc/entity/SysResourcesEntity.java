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

    @Id
    @Column(name = "SYS_RESOURCES_ID", nullable = false, length = 8)
    public String getSysResourcesId() {
        return sysResourcesId;
    }

    @Column(name = "TYPE", length = 4)
    @Basic
    public String getType() {
        return type;
    }

    @Column(name = "NAME", length = 24)
    @Basic
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "DESC", length = 200)
    public String getDesc() {
        return desc;
    }

    @Basic
    @Column(name = "PATH", length = 200)
    public String getPath() {
        return path;
    }

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
