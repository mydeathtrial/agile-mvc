package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：[系统管理]定时任务
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_task")
@Remark("[系统管理]定时任务")
public class SysTaskEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String sysTaskId;
    @Remark("定时任务名")
    private String name;
    @Remark("状态")
    private Boolean state;
    @Remark("定时表达式")
    private String cron;
    @Remark("是否同步")
    private Boolean sync;
    @Remark("更新时间")
    private Date updateTime;
    @Remark("创建时间")
    private Date createTime;

    @Column(name = "sys_task_id", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysTaskId() {
        return sysTaskId;
    }

    @Column(name = "name", length = 255)
    @Basic
    @Length(max = 255, message = "最长为255个字符", groups = {Insert.class, Update.class})
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "state", length = 1)
    public Boolean getState() {
        return state;
    }

    @Column(name = "cron", length = 36)
    @Length(max = 36, message = "最长为36个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getCron() {
        return cron;
    }

    @Basic
    @Column(name = "sync", length = 1)
    public Boolean getSync() {
        return sync;
    }

    @UpdateTimestamp
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Past
    @CreationTimestamp
    @Column(name = "create_time", length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }


    @Override
    public SysTaskEntity clone() {
        try {
            return (SysTaskEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
