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
import org.hibernate.annotations.GenerationTime;
import java.util.Date;
import org.hibernate.annotations.Generated;

/**
 * 描述：[系统管理]定时任务
 * @author agile gennerator
 */
@Entity
@Table(name = "sys_task")
@Remark("[系统管理]定时任务")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysTaskEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
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

    /**
     * 无参构造器
     */
    public SysTaskEntity() { }

    /**
     * 带参构造器
     */
    public SysTaskEntity(String sysTaskId, String name, Boolean state, String cron, Boolean sync, Date updateTime, Date createTime) {
        this.sysTaskId = sysTaskId;
        this.name = name;
        this.state = state;
        this.cron = cron;
        this.sync = sync;
        this.updateTime = updateTime;
        this.createTime = createTime;
    }

    @Id
    @Column(name = "sys_task_id", nullable = false)
    public String getSysTaskId() {
        return sysTaskId;
    }

    public void setSysTaskId(String sysTaskId) {
    this.sysTaskId = sysTaskId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
    this.name = name;
    }

    @Basic
    @Column(name = "state")
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
    this.state = state;
    }

    @Basic
    @Column(name = "cron")
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
    this.cron = cron;
    }

    @Basic
    @Column(name = "sync")
    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
    this.sync = sync;
    }

    @Basic
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
    }

    @Basic
    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
    this.createTime = createTime;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysTaskEntity)) {
            return false;
        }
        SysTaskEntity that = (SysTaskEntity) object;
        return Objects.equals(getSysTaskId(), that.getSysTaskId())
        && Objects.equals(getName(), that.getName())
        && Objects.equals(getState(), that.getState())
        && Objects.equals(getCron(), that.getCron())
        && Objects.equals(getSync(), that.getSync())
        && Objects.equals(getUpdateTime(), that.getUpdateTime())
        && Objects.equals(getCreateTime(), that.getCreateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysTaskId(), getName(), getState(), getCron(), getSync(), getUpdateTime(), getCreateTime());
    }

    @Override
    public String toString() {
    return "SysTaskEntity{"
            + "sysTaskId='" + sysTaskId + '\''
            + ",name='" + name + '\''
            + ",state=" + state
            + ",cron='" + cron + '\''
            + ",sync=" + sync
            + ",updateTime=" + updateTime
            + ",createTime=" + createTime + '}';
    }

    private SysTaskEntity(Builder builder) {
        this.sysTaskId = builder.sysTaskId;
        this.name = builder.name;
        this.state = builder.state;
        this.cron = builder.cron;
        this.sync = builder.sync;
        this.updateTime = builder.updateTime;
        this.createTime = builder.createTime;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String sysTaskId;
        private String name;
        private Boolean state;
        private String cron;
        private Boolean sync;
        private Date updateTime;
        private Date createTime;

        public Builder setSysTaskId(String sysTaskId) {
            this.sysTaskId = sysTaskId;
            return this;
        }
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setState(Boolean state) {
            this.state = state;
            return this;
        }
        public Builder setCron(String cron) {
            this.cron = cron;
            return this;
        }
        public Builder setSync(Boolean sync) {
            this.sync = sync;
            return this;
        }
        public Builder setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }
        public Builder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }
        public SysTaskEntity build() {
            return new SysTaskEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
