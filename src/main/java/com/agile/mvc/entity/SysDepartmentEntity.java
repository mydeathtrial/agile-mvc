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
import java.util.Date;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.TemporalType;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysDepartId() {
        return sysDepartId;
    }

    @Basic
    @Column(name = "parent_id", length = 18)
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @Basic
    @Length(max = 20, message = "最长为20个字符", groups = {Insert.class, Update.class})
    @Column(name = "depart_name", length = 20)
    public String getDepartName() {
        return departName;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "depart_desc", length = 100)
    public String getDepartDesc() {
        return departDesc;
    }

    @Length(max = 10, message = "最长为10个字符", groups = {Insert.class, Update.class})
    @Column(name = "sort", length = 10)
    @Basic
    public Integer getSort() {
        return sort;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @UpdateTimestamp
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time", length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    @Column(name = "enable", length = 1)
    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
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
