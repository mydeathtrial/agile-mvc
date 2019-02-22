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
import javax.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Delete;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.apache.ibatis.annotations.Update;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;
import org.hibernate.validator.constraints.Length;
import org.apache.ibatis.annotations.Insert;

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
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getLogTableId() {
        return logTableId;
    }

    @Basic
    @NotBlank(message = "日志标识不能为空", groups = {Insert.class, Update.class})
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    @Column(name = "log_main_id", nullable = false, length = 18)
    public String getLogMainId() {
        return logMainId;
    }

    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Column(name = "table_schema", nullable = false, length = 64)
    @Basic
    @NotBlank(message = "数据库不能为空", groups = {Insert.class, Update.class})
    public String getTableSchema() {
        return tableSchema;
    }

    @NotBlank(message = "表名不能为空", groups = {Insert.class, Update.class})
    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "table_name", nullable = false, length = 64)
    public String getTableName() {
        return tableName;
    }

    @NotBlank(message = "操作类型不能为空", groups = {Insert.class, Update.class})
    @Column(name = "operation_type", nullable = false, length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getOperationType() {
        return operationType;
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
