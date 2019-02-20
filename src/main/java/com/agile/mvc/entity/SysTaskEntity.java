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
import org.hibernate.annotations.GenerationTime;
import java.util.Date;
import org.hibernate.annotations.Generated;
import javax.persistence.Id;

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

    @Id
    @Column(name = "sys_task_id", nullable = false, length = 8)
    public String getSysTaskId() {
        return sysTaskId;
    }

    @Column(name = "name", length = 255)
    @Basic
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "state", length = 1)
    public Boolean getState() {
        return state;
    }

    @Column(name = "cron", length = 36)
    @Basic
    public String getCron() {
        return cron;
    }

    @Basic
    @Column(name = "sync", length = 1)
    public Boolean getSync() {
        return sync;
    }

    @Basic
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time", length = 26)
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
