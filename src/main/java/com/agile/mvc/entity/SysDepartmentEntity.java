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
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.TemporalType;
import org.apache.ibatis.annotations.Delete;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.agile.common.annotation.Remark;
import javax.validation.constraints.Past;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import org.apache.ibatis.annotations.Update;
import javax.persistence.Id;
import org.hibernate.validator.constraints.Length;
import org.apache.ibatis.annotations.Insert;

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
    @Builder.Default
    private String parentId = "root";
    @Remark("名字")
    private String departName;
    @Remark("描述")
    private String departDesc;
    @Remark("排序")
    private Integer sort;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("更新时间")
    private Date updateTime;
    @Remark("创建时间")
    private Date createTime;

    @Column(name = "sys_depart_id", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysDepartId() {
        return sysDepartId;
    }

    @Column(name = "parent_id", columnDefinition = "VARCHAR default root", length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @Basic
    @Column(name = "depart_name", nullable = false, length = 20)
    @Length(max = 20, message = "最长为20个字符", groups = {Insert.class, Update.class})
    @NotBlank(message = "名字不能为空", groups = {Insert.class, Update.class})
    public String getDepartName() {
        return departName;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "depart_desc", length = 100)
    public String getDepartDesc() {
        return departDesc;
    }

    @Column(name = "sort", columnDefinition = "TINYINT default 0", length = 3)
    @Min(value = 0, groups = {Insert.class, Update.class})
    @Basic
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
    public Integer getSort() {
        return sort;
    }

    @Basic
    @Column(name = "enable", columnDefinition = "BIT default 0", length = 1)
    public Boolean getEnable() {
        return enable;
    }

    @UpdateTimestamp
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", length = 26)
    public Date getUpdateTime() {
        return updateTime;
    }

    @NotNull(message = "创建时间不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Past
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
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
