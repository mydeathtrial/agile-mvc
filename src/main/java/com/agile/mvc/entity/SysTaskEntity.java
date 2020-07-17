package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.util.PropertiesUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：[系统管理]定时任务
 *
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sys_task")
@Remark("[系统管理]定时任务")
public class SysTaskEntity implements Serializable, Cloneable, Task {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private Long sysTaskId;
    @Remark("定时任务名")
    private String name;
    @Remark("状态")
    private String status;
    @Remark("启动")
    private Boolean enable;
    @Remark("定时表达式")
    private String cron;
    @Remark("是否同步")
    private Boolean sync;
    @Remark("更新时间")
    private Date updateTime;
    @Remark("创建时间")
    private Date createTime;
    @Remark("应用名字")
    @Builder.Default
    private String application = PropertiesUtil.getProperty("spring.application.name");

    private List<SysApiEntity> targets;

    @Column(name = "sys_task_id", nullable = false, length = 19)
    @Id
    public Long getSysTaskId() {
        return sysTaskId;
    }

    @Override
    @Column(name = "name", length = 255)
    @Basic
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "status", length = 4)
    public String getStatus() {
        return status;
    }

    @Transient
    @Override
    public Long getCode() {
        return sysTaskId;
    }

    @Override
    @Column(name = "cron", length = 36)
    @Basic
    public String getCron() {
        return cron;
    }

    @Override
    @Basic
    @Column(name = "sync", length = 1)
    public Boolean getSync() {
        return sync;
    }

    @Basic
    @Column(name = "enable", length = 1)
    @Override
    public Boolean getEnable() {
        return enable;
    }

    @Override
    public List<Target> targets() {
        return targets == null ? new ArrayList<>() : new ArrayList<>(targets);
    }

    @Transient
    @Override
    public String getArgument() {
        return getCode().toString();
    }

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "sys_bt_task_api", joinColumns = @JoinColumn(name = "sys_task_id"), inverseJoinColumns = @JoinColumn(name = "sys_api_id"))
    public List<SysApiEntity> getTargets() {
        return targets;
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

    @Column(name = "application", length = 255)
    @Basic
    public String getApplication() {
        return application;
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
