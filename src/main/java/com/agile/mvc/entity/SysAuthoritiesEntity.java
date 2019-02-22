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
import org.apache.ibatis.annotations.Update;
import javax.persistence.Id;
import org.hibernate.validator.constraints.Length;
import org.apache.ibatis.annotations.Insert;

/**
 * 描述：[系统管理]权限
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_authorities")
@Remark("[系统管理]权限")
public class SysAuthoritiesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysAuthorityId;
    @Remark("权限标识")
    private String mark;
    @Remark("权限名称")
    private String name;
    @Remark("权限说明")
    private String desc;
    @Remark("是否可用")
    private Boolean enable;

    @Column(name = "SYS_AUTHORITY_ID", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysAuthorityId() {
        return sysAuthorityId;
    }

    @Column(name = "MARK", length = 8)
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getMark() {
        return mark;
    }

    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Basic
    @NotBlank(message = "权限名称不能为空", groups = {Insert.class, Update.class})
    @Column(name = "NAME", nullable = false, length = 24)
    public String getName() {
        return name;
    }

    @Length(max = 200, message = "最长为200个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "DESC", length = 200)
    public String getDesc() {
        return desc;
    }

    @Column(name = "ENABLE", columnDefinition = "BIT default 0", length = 1)
    @Basic
    public Boolean getEnable() {
        return enable;
    }


    @Override
    public SysAuthoritiesEntity clone() {
        try {
            return (SysAuthoritiesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
