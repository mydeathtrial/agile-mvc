package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "sys_bt_task_api_id", nullable = false, length = 19)
    @Id
    public Long getSysBtTaskApiId() {
        return sysBtTaskApiId;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "sys_task_id", nullable = false, length = 19)
    @NotNull(message = "定时任务标志不能为空", groups = {Insert.class, Update.class})
    public Long getSysTaskId() {
        return sysTaskId;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "sys_api_id", nullable = false, length = 19)
    @Basic
    @NotNull(message = "目标方法主键不能为空", groups = {Insert.class, Update.class})
    public Long getSysApiId() {
        return sysApiId;
    }

    @Min(value = 0, groups = {Insert.class, Update.class})
    @Column(name = "`order`", length = 3)
    @Basic
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
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
