package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "log_table", catalog = "agile_db")
@Remark("[系统管理]日志相关表变动信息")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LogTableEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String logTableId;
    @Remark("日志标识")
    private String logMainId;
    @Remark("数据库")
    private String tableSchema;
    @Remark("表名")
    private String tableName;
    @Remark("操作类型")
    private String operationType;
    @Remark("操作顺序")
    private Integer operationOrder;

    //无参构造器
    public LogTableEntity() {
    }

    //有参构造器
    public LogTableEntity(String logTableId, String logMainId, String tableSchema, String tableName, String operationType, Integer operationOrder) {
        this.logTableId = logTableId;
        this.logMainId = logMainId;
        this.tableSchema = tableSchema;
        this.tableName = tableName;
        this.operationType = operationType;
        this.operationOrder = operationOrder;
    }

    @Id
    @Column(name = "log_table_id", nullable = false)
    public String getLogTableId() {
        return logTableId;
    }

    public void setLogTableId(String logTableId) {
        this.logTableId = logTableId;
    }

    @Basic
    @Column(name = "log_main_id", nullable = false)
    public String getLogMainId() {
        return logMainId;
    }

    public void setLogMainId(String logMainId) {
        this.logMainId = logMainId;
    }

    @Basic
    @Column(name = "table_schema", nullable = false)
    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Basic
    @Column(name = "table_name", nullable = false)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Basic
    @Column(name = "operation_type", nullable = false)
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Basic
    @Column(name = "operation_order", nullable = false)
    public Integer getOperationOrder() {
        return operationOrder;
    }

    public void setOperationOrder(Integer operationOrder) {
        this.operationOrder = operationOrder;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LogTableEntity)) {
            return false;
        }
        LogTableEntity that = (LogTableEntity) object;
        return Objects.equals(getLogTableId(), that.getLogTableId()) &&
                Objects.equals(getLogMainId(), that.getLogMainId()) &&
                Objects.equals(getTableSchema(), that.getTableSchema()) &&
                Objects.equals(getTableName(), that.getTableName()) &&
                Objects.equals(getOperationType(), that.getOperationType()) &&
                Objects.equals(getOperationOrder(), that.getOperationOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogTableId(), getLogMainId(), getTableSchema(), getTableName(), getOperationType(), getOperationOrder());
    }

    @Override
    public String toString() {
        return "LogTableEntity{" +
                "logTableId='" + logTableId + '\'' +
                ",logMainId='" + logMainId + '\'' +
                ",tableSchema='" + tableSchema + '\'' +
                ",tableName='" + tableName + '\'' +
                ",operationType='" + operationType + '\'' +
                ",operationOrder=" + operationOrder +
                '}';
    }

    private LogTableEntity(Builder builder) {
        this.logTableId = builder.logTableId;
        this.logMainId = builder.logMainId;
        this.tableSchema = builder.tableSchema;
        this.tableName = builder.tableName;
        this.operationType = builder.operationType;
        this.operationOrder = builder.operationOrder;
    }

    public static class Builder {
        private String logTableId;
        private String logMainId;
        private String tableSchema;
        private String tableName;
        private String operationType;
        private Integer operationOrder;

        public Builder setLogTableId(String logTableId) {
            this.logTableId = logTableId;
            return this;
        }

        public Builder setLogMainId(String logMainId) {
            this.logMainId = logMainId;
            return this;
        }

        public Builder setTableSchema(String tableSchema) {
            this.tableSchema = tableSchema;
            return this;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setOperationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder setOperationOrder(Integer operationOrder) {
            this.operationOrder = operationOrder;
            return this;
        }

        public LogTableEntity build() {
            return new LogTableEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
