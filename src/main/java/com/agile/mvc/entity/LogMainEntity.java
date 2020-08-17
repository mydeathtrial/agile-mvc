package com.agile.mvc.entity;

import cloud.agileframework.generator.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

    @Id
    @Column(name = "log_main_id", nullable = false, length = 19)
    public Long getLogMainId() {
        return logMainId;
    }

    @Column(name = "sys_resources_id", length = 19)
    @Basic
    public Long getSysResourcesId() {
        return sysResourcesId;
    }

    @Basic
    @Column(name = "in_param", length = 65535)
    public String getInParam() {
        return inParam;
    }

    @Basic
    @Column(name = "out_param", length = 65535)
    public String getOutParam() {
        return outParam;
    }

    @Column(name = "ip", length = 15)
    @Basic
    public String getIp() {
        return ip;
    }

    @Column(name = "time_consuming", nullable = false, columnDefinition = "INT UNSIGNED default 0", length = 10)
    @Basic
    public Long getTimeConsuming() {
        return timeConsuming;
    }

    @Basic
    @Column(name = "status", nullable = false, columnDefinition = "BIT default 1", length = 1)
    public Boolean getStatus() {
        return status;
    }

    @Column(name = "target_type", length = 4)
    @Basic
    public String getTargetType() {
        return targetType;
    }

    @Column(name = "target_code", length = 19)
    @Basic
    public Long getTargetCode() {
        return targetCode;
    }

    @Basic
    @Column(name = "user_account_number", nullable = false, length = 40)
    public String getUserAccountNumber() {
        return userAccountNumber;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
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
