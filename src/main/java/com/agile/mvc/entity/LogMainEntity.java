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
import org.hibernate.annotations.GenerationTime;
import java.util.Date;
import org.hibernate.annotations.Generated;
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
    @Remark("业务对象类型")
    private String targetType;
    @Remark("业务对象标识")
    private String targetCode;
    @Remark("操作人")
    private String userId;
    @Remark("操作时间")
    private Date createTime;

    @Column(name = "log_main_id", nullable = false)
    @Id
    public String getLogMainId() {
        return logMainId;
    }

    @Column(name = "business_code", nullable = false)
    @Basic
    public String getBusinessCode() {
        return businessCode;
    }

    @Column(name = "target_type")
    @Basic
    public String getTargetType() {
        return targetType;
    }

    @Column(name = "target_code")
    @Basic
    public String getTargetCode() {
        return targetCode;
    }

    @Column(name = "user_id", nullable = false)
    @Basic
    public String getUserId() {
        return userId;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time", nullable = false)
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
