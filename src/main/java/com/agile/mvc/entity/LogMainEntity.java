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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：[系统管理]日志表
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
@Table(name = "log_main")
@Remark("[系统管理]日志表")
public class LogMainEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private Long logMainId;
    @Remark("资源主键")
    private Long sysResourcesId;
    @Remark("入参")
    private String inParam;
    @Remark("出参")
    private String outParam;
    @Remark("IP地址")
    private String ip;
    @Remark("耗时")
    private Long timeConsuming;
    @Remark("结果状态")
    private Boolean status;
    @Remark("业务对象类型")
    private String targetType;
    @Remark("业务对象标识")
    private Long targetCode;
    @Remark("操作人")
    private String userAccountNumber;
    @Remark("操作时间")
    private Date createTime;

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Id
    @Column(name = "log_main_id", nullable = false, length = 19)
    public Long getLogMainId() {
        return logMainId;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "sys_resources_id", length = 19)
    @Basic
    public Long getSysResourcesId() {
        return sysResourcesId;
    }

    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "in_param", length = 65535)
    public String getInParam() {
        return inParam;
    }

    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "out_param", length = 65535)
    public String getOutParam() {
        return outParam;
    }

    @Length(max = 15, message = "最长为15个字符", groups = {Insert.class, Update.class})
    @Column(name = "ip", length = 15)
    @Basic
    public String getIp() {
        return ip;
    }

    @Column(name = "time_consuming", nullable = false, columnDefinition = "INT UNSIGNED default 0", length = 10)
    @Min(value = 0, groups = {Insert.class, Update.class})
    @Basic
    @NotNull(message = "耗时不能为空", groups = {Insert.class, Update.class})
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
    public Long getTimeConsuming() {
        return timeConsuming;
    }

    @NotNull(message = "结果状态不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "status", nullable = false, columnDefinition = "BIT default 1", length = 1)
    public Boolean getStatus() {
        return status;
    }

    @Column(name = "target_type", length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getTargetType() {
        return targetType;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "target_code", length = 19)
    @Basic
    public Long getTargetCode() {
        return targetCode;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Basic
    @NotNull(message = "操作人不能为空", groups = {Insert.class, Update.class})
    @Column(name = "user_account_number", nullable = false, length = 40)
    public String getUserAccountNumber() {
        return userAccountNumber;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "操作时间不能为空", groups = {Insert.class, Update.class})
    @Past
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }


    @Override
    public LogMainEntity clone() {
        try {
            return (LogMainEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
