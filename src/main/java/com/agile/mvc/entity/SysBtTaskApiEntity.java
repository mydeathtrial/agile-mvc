package com.agile.mvc.entity;

import cloud.agileframework.generator.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 描述：[系统管理]定时任务目标任务表
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
@Table(name = "sys_bt_task_api")
@Remark("[系统管理]定时任务目标任务表")
public class SysBtTaskApiEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private Long sysBtTaskApiId;
    @Remark("定时任务标志")
    private Long sysTaskId;
    @Remark("目标方法主键")
    private Long sysApiId;
    @Remark("优先级")
    private Integer order;

    @Column(name = "sys_bt_task_api_id", nullable = false, length = 19)
    @Id
    public Long getSysBtTaskApiId() {
        return sysBtTaskApiId;
    }

    @Basic
    @Column(name = "sys_task_id", nullable = false, length = 19)
    public Long getSysTaskId() {
        return sysTaskId;
    }

    @Column(name = "sys_api_id", nullable = false, length = 19)
    @Basic
    public Long getSysApiId() {
        return sysApiId;
    }

    @Column(name = "`order`", length = 3)
    @Basic
    public Integer getOrder() {
        return order;
    }


    @Override
    public SysBtTaskApiEntity clone() {
        try {
            return (SysBtTaskApiEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
