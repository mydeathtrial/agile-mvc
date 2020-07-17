package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "log_value_id", nullable = false, length = 19)
    @Id
    public Long getLogValueId() {
        return logValueId;
    }

    @Column(name = "log_table_id", nullable = false, length = 19)
    @Basic
    public Long getLogTableId() {
        return logTableId;
    }

    @Basic
    @Column(name = "column_name", nullable = false, length = 64)
    public String getColumnName() {
        return columnName;
    }

    @Column(name = "column_type", nullable = false, length = 64)
    @Basic
    public String getColumnType() {
        return columnType;
    }

    @Column(name = "new_value", length = 100)
    @Basic
    public String getNewValue() {
        return newValue;
    }

    @Basic
    @Column(name = "old_value", length = 100)
    public String getOldValue() {
        return oldValue;
    }

    @Column(name = "column_info", length = 64)
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
