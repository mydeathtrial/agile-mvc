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
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.TemporalType;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    @Column(name = "log_main_id", nullable = false, length = 18)
    public String getLogMainId() {
        return logMainId;
    }

    @Column(name = "business_code", nullable = false, length = 6)
    @Basic
    @Length(max = 6, message = "最长为6个字符", groups = {Insert.class, Update.class})
    @NotEmpty(message = "业务编码不能为空", groups = {Insert.class, Update.class})
    public String getBusinessCode() {
        return businessCode;
    }

    @Column(name = "target_type", length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getTargetType() {
        return targetType;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "target_code", length = 100)
    public String getTargetCode() {
        return targetCode;
    }

    @NotEmpty(message = "操作人不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    @Column(name = "user_id", nullable = false, length = 8)
    public String getUserId() {
        return userId;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @NotEmpty(message = "操作时间不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
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
