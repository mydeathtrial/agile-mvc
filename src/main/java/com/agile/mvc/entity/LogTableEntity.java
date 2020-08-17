package com.agile.mvc.entity;

import cloud.agileframework.generator.annotation.Remark;
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

    @Column(name = "log_table_id", nullable = false, length = 19)
    @Id
    public Long getLogTableId() {
        return logTableId;
    }

    @Basic
    @Column(name = "log_main_id", nullable = false, length = 19)
    public Long getLogMainId() {
        return logMainId;
    }

    @Column(name = "`sql`", nullable = false, length = 64)
    @Basic
    public String getSql() {
        return sql;
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
