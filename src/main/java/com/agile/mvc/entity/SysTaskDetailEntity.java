package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：[系统管理]定时任务详情
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
@Table(name = "sys_task_detail")
@Remark("[系统管理]定时任务详情")
public class SysTaskDetailEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private Long sysTaskInfoId;
    @Remark("任务主键")
    private Long sysTaskId;
    @Remark("执行状态")
    private Boolean ending;
    @Remark("开始时间")
    private Date startTime;
    @Remark("结束时间")
    private Date endTime;
    @Remark("日志信息")
    private String log;

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Id
    @Column(name = "sys_task_info_id", nullable = false, length = 19)
    public Long getSysTaskInfoId() {
        return sysTaskInfoId;
    }

    @DecimalMax(value = "9223372036854775807", groups = {Insert.class, Update.class})
    @DecimalMin(value = "0", groups = {Insert.class, Update.class})
    @Column(name = "sys_task_id", length = 19)
    @Basic
    public Long getSysTaskId() {
        return sysTaskId;
    }

    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    @Column(name = "ending", length = 1)
    public Boolean getEnding() {
        return ending;
    }

    @Basic
    @Column(name = "start_time", length = 26)
    public Date getStartTime() {
        return startTime;
    }

    @Column(name = "end_time", length = 26)
    @Basic
    public Date getEndTime() {
        return endTime;
    }

    @Column(name = "log", length = 65535)
    @Length(max = 65535, message = "最长为65535个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getLog() {
        return log;
    }


    @Override
    public SysTaskDetailEntity clone() {
        try {
            return (SysTaskDetailEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
