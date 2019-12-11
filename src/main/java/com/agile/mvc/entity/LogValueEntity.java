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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 描述：[系统管理]日志相关字段值变动信息
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
@Table(name = "log_value")
@Remark("[系统管理]日志相关字段值变动信息")
public class LogValueEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private Long logValueId;
    @Remark("日志相关表标识")
    private Long logTableId;
    @Remark("字段")
    private String columnName;
    @Remark("字段类型")
    private String columnType;
    @Remark("新值")
    private String newValue;
    @Remark("旧值")
    private String oldValue;
    @Remark("字段含义")
    private String columnInfo;

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "log_value_id", nullable = false, length = 19)
    @Id
    public Long getLogValueId() {
        return logValueId;
    }

    @NotNull(message = "日志相关表标识不能为空", groups = {Insert.class, Update.class})
    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "log_table_id", nullable = false, length = 19)
    @Basic
    public Long getLogTableId() {
        return logTableId;
    }

    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @NotBlank(message = "字段不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "column_name", nullable = false, length = 64)
    public String getColumnName() {
        return columnName;
    }

    @NotBlank(message = "字段类型不能为空", groups = {Insert.class, Update.class})
    @Column(name = "column_type", nullable = false, length = 64)
    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getColumnType() {
        return columnType;
    }

    @Column(name = "new_value", length = 100)
    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getNewValue() {
        return newValue;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "old_value", length = 100)
    public String getOldValue() {
        return oldValue;
    }

    @Column(name = "column_info", length = 64)
    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getColumnInfo() {
        return columnInfo;
    }


    @Override
    public LogValueEntity clone() {
        try {
            return (LogValueEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
