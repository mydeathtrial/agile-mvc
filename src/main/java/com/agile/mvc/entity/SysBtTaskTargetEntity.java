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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
    private Integer order = 1;

    @Column(name = "sys_bt_task_target_id", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysBtTaskTargetId() {
        return sysBtTaskTargetId;
    }

    @NotBlank(message = "定时任务标志不能为空", groups = {Insert.class, Update.class})
    @Column(name = "sys_task_id", nullable = false, length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysTaskId() {
        return sysTaskId;
    }

    @Basic
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    @Column(name = "sys_task_target_id", nullable = false, length = 255)
    @NotBlank(message = "目标方法主键不能为空", groups = {Insert.class, Update.class})
    public String getSysTaskTargetId() {
        return sysTaskTargetId;
    }

    @Column(name = "`order`", nullable = false, length = 3)
    @Min(value = 0, groups = {Insert.class, Update.class})
    @Basic
    @NotNull(message = "优先级不能为空", groups = {Insert.class, Update.class})
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
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
