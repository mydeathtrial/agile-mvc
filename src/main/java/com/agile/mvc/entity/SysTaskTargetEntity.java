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
 * 描述：[系统管理]目标任务表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_task_target")
@Remark("[系统管理]目标任务表")
public class SysTaskTargetEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysTaskTargetId;
    @Remark("方法含义名")
    private String name;
    @Remark("包名")
    private String targetPackage;
    @Remark("类名")
    private String targetClass;
    @Remark("方法名")
    private String targetMethod;
    @Remark("备注")
    private String remarks;

    @Column(name = "sys_task_target_id", nullable = false)
    @Id
    public String getSysTaskTargetId() {
        return sysTaskTargetId;
    }

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    @Column(name = "target_package", nullable = false)
    @Basic
    public String getTargetPackage() {
        return targetPackage;
    }

    @Column(name = "target_class", nullable = false)
    @Basic
    public String getTargetClass() {
        return targetClass;
    }

    @Column(name = "target_method", nullable = false)
    @Basic
    public String getTargetMethod() {
        return targetMethod;
    }

    @Column(name = "remarks")
    @Basic
    public String getRemarks() {
        return remarks;
    }


    @Override
    public SysTaskTargetEntity clone() {
        try {
            return (SysTaskTargetEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
