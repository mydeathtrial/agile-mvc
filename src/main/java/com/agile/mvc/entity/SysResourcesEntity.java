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

    @Column(name = "SYS_RESOURCES_ID", nullable = false)
    @Id
    public String getSysResourcesId() {
        return sysResourcesId;
    }

    @Basic
    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @Column(name = "DESC")
    @Basic
    public String getDesc() {
        return desc;
    }

    @Column(name = "PATH")
    @Basic
    public String getPath() {
        return path;
    }

    @Column(name = "PRIORITY")
    @Basic
    public String getPriority() {
        return priority;
    }

    @Basic
    @Column(name = "ENABLE")
    public Boolean getEnable() {
        return enable;
    }

    @Basic
    @Column(name = "ISSYS")
    public Boolean getIssys() {
        return issys;
    }

    @Basic
    @Column(name = "MODULE_ID")
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
