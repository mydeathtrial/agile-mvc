package com.agile.mvc.entity;

import cloud.agileframework.generator.annotation.Remark;
import cloud.agileframework.task.Target;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 描述：[系统管理]目标任务表
 *
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sys_api")
@Remark("[系统管理]目标任务表")
public class SysApiEntity implements Serializable, Cloneable, Target {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private Long sysApiId;
    @Remark("api名字")
    private String name;
    @Remark("类型(1:对外restApi；0普通方法)")
    private Boolean type;
    @Remark("功能")
    private String businessName;
    @Remark("业务编码")
    private String businessCode;
    @Remark("备注")
    private String remarks;

    private List<SysTaskEntity> tasks;

    @Transient
    @Override
    public String getCode() {
        if (name != null) {
            return name;
        }
        return null;
    }

    @Transient
    @Override
    public String getArgument() {
        return null;
    }

    @Transient
    @Override
    public int getOrder() {
        return 0;
    }

    @Column(name = "sys_api_id", nullable = false, length = 19)
    @Id
    public Long getSysApiId() {
        return sysApiId;
    }

    @Column(name = "name", length = 65535)
    @Basic
    public String getName() {
        return name;
    }

    @Column(name = "type", length = 1)
    @Basic
    public Boolean getType() {
        return type;
    }

    @Column(name = "business_name", length = 40)
    @Basic
    public String getBusinessName() {
        return businessName;
    }

    @Basic
    @Column(name = "business_code", length = 20)
    public String getBusinessCode() {
        return businessCode;
    }

    @Basic
    @Column(name = "remarks", length = 255)
    public String getRemarks() {
        return remarks;
    }

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "targets", fetch = FetchType.EAGER)
    public List<SysTaskEntity> getTasks() {
        return tasks;
    }

    @Override
    public SysApiEntity clone() {
        try {
            return (SysApiEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
