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
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 描述：[系统管理]日志相关表变动信息
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
@Table(name = "log_table")
@Remark("[系统管理]日志相关表变动信息")
public class LogTableEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private Long logTableId;
    @Remark("日志标识")
    private Long logMainId;
    @Remark("sql语句")
    private String sql;
    @Remark("操作顺序")
    private Integer operationOrder;

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "log_table_id", nullable = false, length = 19)
    @Id
    public Long getLogTableId() {
        return logTableId;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @NotNull(message = "日志标识不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "log_main_id", nullable = false, length = 19)
    public Long getLogMainId() {
        return logMainId;
    }

    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Column(name = "`sql`", nullable = false, length = 64)
    @Basic
    @NotBlank(message = "数据库不能为空", groups = {Insert.class, Update.class})
    public String getSql() {
        return sql;
    }

    @Min(value = 0, groups = {Insert.class, Update.class})
    @Basic
    @NotNull(message = "操作顺序不能为空", groups = {Insert.class, Update.class})
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
    @Column(name = "operation_order", nullable = false, length = 10)
    public Integer getOperationOrder() {
        return operationOrder;
    }


    @Override
    public LogTableEntity clone() {
        try {
            return (LogTableEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
