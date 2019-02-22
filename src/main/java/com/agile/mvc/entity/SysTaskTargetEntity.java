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

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    @Column(name = "sys_task_target_id", nullable = false, length = 255)
    public String getSysTaskTargetId() {
        return sysTaskTargetId;
    }

    @Column(name = "name", length = 255)
    @Basic
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    public String getName() {
        return name;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "target_package", nullable = false, length = 100)
    @NotBlank(message = "包名不能为空", groups = {Insert.class, Update.class})
    public String getTargetPackage() {
        return targetPackage;
    }

    @Column(name = "target_class", nullable = false, length = 40)
    @Basic
    @NotBlank(message = "类名不能为空", groups = {Insert.class, Update.class})
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    public String getTargetClass() {
        return targetClass;
    }

    @NotBlank(message = "方法名不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "target_method", nullable = false, length = 40)
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    public String getTargetMethod() {
        return targetMethod;
    }

    @Basic
    @Column(name = "remarks", length = 255)
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
