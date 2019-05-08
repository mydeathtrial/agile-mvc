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
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import javax.persistence.TemporalType;
import org.apache.ibatis.annotations.Delete;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.agile.common.annotation.Remark;
import javax.validation.constraints.Past;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Id;

/**
 * 描述：[系统管理]日志表
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
    private String logMainId;
    @Remark("业务编码")
    private String businessCode;
    @Remark("请求地址")
    private String url;
    @Remark("bean名")
    private String bean;
    @Remark("方法名")
    private String method;
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
    private String targetCode;
    @Remark("操作人")
    private String userId;
    @Remark("操作时间")
    private Date createTime;

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    @Column(name = "log_main_id", nullable = false, length = 18)
    public String getLogMainId() {
        return logMainId;
    }

    @Column(name = "business_code", nullable = false, length = 6)
    @Basic
    @NotBlank(message = "业务编码不能为空", groups = {Insert.class, Update.class})
    @Length(max = 6, message = "最长为6个字符", groups = {Insert.class, Update.class})
    public String getBusinessCode() {
        return businessCode;
    }

    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "url", columnDefinition = "TEXT default NULL", length = 65535)
    public String getUrl() {
        return url;
    }

    @Column(name = "bean", columnDefinition = "VARCHAR default NULL", length = 40)
    @Basic
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    public String getBean() {
        return bean;
    }

    @Basic
    @Column(name = "method", columnDefinition = "VARCHAR default NULL", length = 40)
    @Length(max = 40, message = "最长为40个字符", groups = {Insert.class, Update.class})
    public String getMethod() {
        return method;
    }

    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "in_param", columnDefinition = "TEXT default NULL", length = 65535)
    public String getInParam() {
        return inParam;
    }

    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "out_param", columnDefinition = "TEXT default NULL", length = 65535)
    public String getOutParam() {
        return outParam;
    }

    @Column(name = "ip", columnDefinition = "VARCHAR default NULL", length = 15)
    @Length(max = 15, message = "最长为15个字符", groups = {Insert.class, Update.class})
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

    @Column(name = "target_type", columnDefinition = "VARCHAR default NULL", length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getTargetType() {
        return targetType;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "target_code", columnDefinition = "VARCHAR default NULL", length = 100)
    public String getTargetCode() {
        return targetCode;
    }

    @NotBlank(message = "操作人不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    @Column(name = "user_id", nullable = false, length = 8)
    public String getUserId() {
        return userId;
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
