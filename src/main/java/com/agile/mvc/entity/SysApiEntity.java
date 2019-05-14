package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import com.agile.common.task.Target;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
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

    @Transient
    @Override
    public String getCode() {
        if (name != null) {
            return name;
        }
        return null;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "sys_api_id", nullable = false, length = 19)
    @Id
    public Long getSysApiId() {
        return sysApiId;
    }

    @Column(name = "name", length = 65535)
    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
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
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    public String getBusinessName() {
        return businessName;
    }

    @Basic
    @Column(name = "business_code", length = 20)
    @Length(max = 20, message = "最长为20个字符", groups = {Insert.class, Update.class})
    public String getBusinessCode() {
        return businessCode;
    }

    @Basic
    @Column(name = "remarks", length = 255)
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    public String getRemarks() {
        return remarks;
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
