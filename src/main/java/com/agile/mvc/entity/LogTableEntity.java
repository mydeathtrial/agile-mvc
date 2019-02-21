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
 * 描述：[系统管理]日志相关表变动信息
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

    @Column(name = "log_table_id", nullable = false, length = 18)
    @Id
    public String getLogTableId() {
        return logTableId;
    }

    @Basic
    @Column(name = "log_main_id", nullable = false, length = 18)
    public String getLogMainId() {
        return logMainId;
    }

    @Column(name = "table_schema", nullable = false, length = 64)
    @Basic
    public String getTableSchema() {
        return tableSchema;
    }

    @Basic
    @Column(name = "table_name", nullable = false, length = 64)
    public String getTableName() {
        return tableName;
    }

    @Column(name = "operation_type", nullable = false, length = 4)
    @Basic
    public String getOperationType() {
        return operationType;
    }

    @Basic
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
