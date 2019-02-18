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
import javax.persistence.Id;

/**
 * 描述：[系统管理]日志相关字段值变动信息
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
    private String logValueId;
    @Remark("日志相关表标识")
    private String logTableId;
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

    @Column(name = "log_value_id", nullable = false)
    @Id
    public String getLogValueId() {
        return logValueId;
    }

    @Column(name = "log_table_id", nullable = false)
    @Basic
    public String getLogTableId() {
        return logTableId;
    }

    @Column(name = "column_name", nullable = false)
    @Basic
    public String getColumnName() {
        return columnName;
    }

    @Column(name = "column_type", nullable = false)
    @Basic
    public String getColumnType() {
        return columnType;
    }

    @Basic
    @Column(name = "new_value")
    public String getNewValue() {
        return newValue;
    }

    @Column(name = "old_value")
    @Basic
    public String getOldValue() {
        return oldValue;
    }

    @Basic
    @Column(name = "column_info")
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
