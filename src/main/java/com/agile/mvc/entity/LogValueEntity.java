package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 描述：[系统管理]日志相关字段值变动信息
 * @author agile gennerator
 */
@Entity
@Table(name = "log_value")
@Remark("[系统管理]日志相关字段值变动信息")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LogValueEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
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

    /**
     * 无参构造器
     */
    public LogValueEntity() { }

    /**
     * 带参构造器
     */
    public LogValueEntity(String logValueId, String logTableId, String columnName, String columnType, String newValue, String oldValue, String columnInfo) {
        this.logValueId = logValueId;
        this.logTableId = logTableId;
        this.columnName = columnName;
        this.columnType = columnType;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.columnInfo = columnInfo;
    }

    @Id
    @Column(name = "log_value_id", nullable = false)
    public String getLogValueId() {
        return logValueId;
    }

    public void setLogValueId(String logValueId) {
    this.logValueId = logValueId;
    }

    @Basic
    @Column(name = "log_table_id", nullable = false)
    public String getLogTableId() {
        return logTableId;
    }

    public void setLogTableId(String logTableId) {
    this.logTableId = logTableId;
    }

    @Basic
    @Column(name = "column_name", nullable = false)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
    this.columnName = columnName;
    }

    @Basic
    @Column(name = "column_type", nullable = false)
    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
    this.columnType = columnType;
    }

    @Basic
    @Column(name = "new_value")
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
    this.newValue = newValue;
    }

    @Basic
    @Column(name = "old_value")
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
    }

    @Basic
    @Column(name = "column_info")
    public String getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(String columnInfo) {
    this.columnInfo = columnInfo;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LogValueEntity)) {
            return false;
        }
        LogValueEntity that = (LogValueEntity) object;
        return Objects.equals(getLogValueId(), that.getLogValueId())
        && Objects.equals(getLogTableId(), that.getLogTableId())
        && Objects.equals(getColumnName(), that.getColumnName())
        && Objects.equals(getColumnType(), that.getColumnType())
        && Objects.equals(getNewValue(), that.getNewValue())
        && Objects.equals(getOldValue(), that.getOldValue())
        && Objects.equals(getColumnInfo(), that.getColumnInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogValueId(), getLogTableId(), getColumnName(), getColumnType(), getNewValue(), getOldValue(), getColumnInfo());
    }

    @Override
    public String toString() {
    return "LogValueEntity{"
            + "logValueId ='" + logValueId + '\''
            + ",logTableId ='" + logTableId + '\''
            + ",columnName ='" + columnName + '\''
            + ",columnType ='" + columnType + '\''
            + ",newValue ='" + newValue + '\''
            + ",oldValue ='" + oldValue + '\''
            + ",columnInfo ='" + columnInfo + '\'' + '}';
    }

    private LogValueEntity(Builder builder) {
        this.logValueId = builder.logValueId;
        this.logTableId = builder.logTableId;
        this.columnName = builder.columnName;
        this.columnType = builder.columnType;
        this.newValue = builder.newValue;
        this.oldValue = builder.oldValue;
        this.columnInfo = builder.columnInfo;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String logValueId;
        private String logTableId;
        private String columnName;
        private String columnType;
        private String newValue;
        private String oldValue;
        private String columnInfo;

        public Builder setLogValueId(String logValueId) {
            this.logValueId = logValueId;
            return this;
        }
        public Builder setLogTableId(String logTableId) {
            this.logTableId = logTableId;
            return this;
        }
        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }
        public Builder setColumnType(String columnType) {
            this.columnType = columnType;
            return this;
        }
        public Builder setNewValue(String newValue) {
            this.newValue = newValue;
            return this;
        }
        public Builder setOldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }
        public Builder setColumnInfo(String columnInfo) {
            this.columnInfo = columnInfo;
            return this;
        }
        public LogValueEntity build() {
            return new LogValueEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
