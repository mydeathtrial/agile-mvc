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
 * 描述：[系统管理]定时任务目标任务表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_bt_task_target")
@Remark("[系统管理]定时任务目标任务表")
public class SysBtTaskTargetEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String sysBtTaskTargetId;
    @Remark("定时任务标志")
    private String sysTaskId;
    @Remark("目标方法主键")
    private String sysTaskTargetId;
    @Remark("优先级")
    private Integer order;

    @Column(name = "sys_bt_task_target_id", nullable = false, length = 18)
    @Id
    public String getSysBtTaskTargetId() {
        return sysBtTaskTargetId;
    }

    @Column(name = "sys_task_id", nullable = false, length = 18)
    @Basic
    public String getSysTaskId() {
        return sysTaskId;
    }

    @Basic
    @Column(name = "sys_task_target_id", nullable = false, length = 255)
    public String getSysTaskTargetId() {
        return sysTaskTargetId;
    }

    @Column(name = "order", nullable = false, length = 3)
    @Basic
    public Integer getOrder() {
        return order;
    }


    @Override
    public SysBtTaskTargetEntity clone() {
        try {
            return (SysBtTaskTargetEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
