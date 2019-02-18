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
 * 描述：[系统管理]模块
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_modules")
@Remark("[系统管理]模块")
public class SysModulesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysModulesId;
    @Remark("模块名称")
    private String name;
    @Remark("模块说明")
    private String desc;
    @Remark("模块上级")
    private String parentId;
    @Remark("模块地址")
    private String url;
    @Remark("级别")
    private String level;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("优先级")
    private Integer order;

    @Column(name = "SYS_MODULES_ID", nullable = false)
    @Id
    public String getSysModulesId() {
        return sysModulesId;
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

    @Column(name = "PARENT_ID")
    @Basic
    public String getParentId() {
        return parentId;
    }

    @Column(name = "URL")
    @Basic
    public String getUrl() {
        return url;
    }

    @Basic
    @Column(name = "LEVEL")
    public String getLevel() {
        return level;
    }

    @Basic
    @Column(name = "ENABLE")
    public Boolean getEnable() {
        return enable;
    }

    @Column(name = "ORDER")
    @Basic
    public Integer getOrder() {
        return order;
    }


    @Override
    public SysModulesEntity clone() {
        try {
            return (SysModulesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
