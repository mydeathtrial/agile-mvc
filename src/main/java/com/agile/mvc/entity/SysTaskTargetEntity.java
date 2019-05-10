package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import com.agile.common.task.Target;
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
@ToString
@Entity
@Table(name = "sys_task_target")
@Remark("[系统管理]目标任务表")
public class SysTaskTargetEntity implements Serializable, Cloneable, Target {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysTaskTargetId;
    @Remark("功能")
    private String businessName;
    @Remark("业务编码")
    private String businessCode;
    @Remark("备注")
    private String remarks;

    @Transient
    @Override
    public String getCode() {
        return sysTaskTargetId;
    }

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    @Column(name = "sys_task_target_id", nullable = false, length = 255)
    public String getSysTaskTargetId() {
        return sysTaskTargetId;
    }

    @Basic
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    @Column(name = "business_name", columnDefinition = "VARCHAR default NULL", length = 40)
    public String getBusinessName() {
        return businessName;
    }

    @Basic
    @Length(max = 20, message = "最长为20个字符", groups = {Insert.class, Update.class})
    @Column(name = "business_code", columnDefinition = "VARCHAR default NULL", length = 20)
    public String getBusinessCode() {
        return businessCode;
    }

    @Column(name = "remarks", columnDefinition = "VARCHAR default NULL", length = 255)
    @Basic
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
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
