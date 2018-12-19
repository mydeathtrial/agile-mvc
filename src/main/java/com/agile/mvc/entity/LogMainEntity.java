package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "log_main", catalog = "agile_db")
@Remark("[系统管理]日志表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LogMainEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String logMainId;
    @Remark("业务编码")
    private String businessCode;
    @Remark("业务对象类型")
    private String targetType;
    @Remark("业务对象标识")
    private String targetCode;
    @Remark("操作人")
    private Integer userId;
    @Remark("操作时间")
    private Date createTime;

    //无参构造器
    public LogMainEntity() {
    }

    //有参构造器
    public LogMainEntity(String logMainId, String businessCode, String targetType, String targetCode, Integer userId, Date createTime) {
        this.logMainId = logMainId;
        this.businessCode = businessCode;
        this.targetType = targetType;
        this.targetCode = targetCode;
        this.userId = userId;
        this.createTime = createTime;
    }

    @Id
    @Column(name = "log_main_id", nullable = false)
    public String getLogMainId() {
        return logMainId;
    }

    public void setLogMainId(String logMainId) {
        this.logMainId = logMainId;
    }

    @Basic
    @Column(name = "business_code", nullable = false)
    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    @Basic
    @Column(name = "target_type", nullable = false)
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Basic
    @Column(name = "target_code", nullable = false)
    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time", nullable = false)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LogMainEntity)) return false;
        LogMainEntity that = (LogMainEntity) object;
        return Objects.equals(getLogMainId(), that.getLogMainId()) &&
                Objects.equals(getBusinessCode(), that.getBusinessCode()) &&
                Objects.equals(getTargetType(), that.getTargetType()) &&
                Objects.equals(getTargetCode(), that.getTargetCode()) &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getCreateTime(), that.getCreateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogMainId(), getBusinessCode(), getTargetType(), getTargetCode(), getUserId(), getCreateTime());
    }

    @Override
    public String toString() {
        return "LogMainEntity{" +
                "logMainId='" + logMainId + '\'' +
                ",businessCode='" + businessCode + '\'' +
                ",targetType='" + targetType + '\'' +
                ",targetCode='" + targetCode + '\'' +
                ",userId=" + userId +
                ",createTime=" + createTime +
                '}';
    }

    private LogMainEntity(Builder builder) {
        this.logMainId = builder.logMainId;
        this.businessCode = builder.businessCode;
        this.targetType = builder.targetType;
        this.targetCode = builder.targetCode;
        this.userId = builder.userId;
        this.createTime = builder.createTime;
    }

    public static class Builder {
        private String logMainId;
        private String businessCode;
        private String targetType;
        private String targetCode;
        private Integer userId;
        private Date createTime;

        public Builder setLogMainId(String logMainId) {
            this.logMainId = logMainId;
            return this;
        }

        public Builder setBusinessCode(String businessCode) {
            this.businessCode = businessCode;
            return this;
        }

        public Builder setTargetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        public Builder setTargetCode(String targetCode) {
            this.targetCode = targetCode;
            return this;
        }

        public Builder setUserId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public LogMainEntity build() {
            return new LogMainEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
