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
import javax.persistence.Id;

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

    @Column(name = "SYS_AUTHORITY_ID", nullable = false)
    @Id
    public String getSysAuthorityId() {
        return sysAuthorityId;
    }

    @Basic
    @Column(name = "MARK")
    public String getMark() {
        return mark;
    }

    @Column(name = "NAME", nullable = false)
    @Basic
    public String getName() {
        return name;
    }

    @Column(name = "DESC")
    @Basic
    public String getDesc() {
        return desc;
    }

    @Basic
    @Column(name = "ENABLE")
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
