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
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;
import javax.persistence.Id;

/**

 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_department")
public class SysDepartmentEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("部门主键")
    private String sysDepartId;
    @Remark("父主键")
    private String parentId;
    @Remark("名字")
    private String departName;
    @Remark("描述")
    private String departDesc;
    @Remark("排序")
    private Integer sort;
    @Remark("更新时间")
    private Date updateTime;
    @Remark("创建时间")
    private Date createTime;
    @Remark("是否可用")
    private Boolean enable;

    @Column(name = "sys_depart_id", nullable = false, length = 18)
    @Id
    public String getSysDepartId() {
        return sysDepartId;
    }

    @Basic
    @Column(name = "parent_id", length = 18)
    public String getParentId() {
        return parentId;
    }

    @Basic
    @Column(name = "depart_name", length = 20)
    public String getDepartName() {
        return departName;
    }

    @Basic
    @Column(name = "depart_desc", length = 100)
    public String getDepartDesc() {
        return departDesc;
    }

    @Column(name = "sort", length = 10)
    @Basic
    public Integer getSort() {
        return sort;
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
    @CreationTimestamp
    @Column(name = "create_time", length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    @Column(name = "enable", length = 1)
    @Basic
    public Boolean getEnable() {
        return enable;
    }


    @Override
    public SysDepartmentEntity clone() {
        try {
            return (SysDepartmentEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
