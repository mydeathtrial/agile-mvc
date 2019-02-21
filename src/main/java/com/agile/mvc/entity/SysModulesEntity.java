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
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
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

    @Column(name = "SYS_MODULES_ID", nullable = false, length = 18)
    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysModulesId() {
        return sysModulesId;
    }

    @NotEmpty(message = "模块名称不能为空", groups = {Insert.class, Update.class})
    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Basic
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

    @Column(name = "PARENT_ID", length = 8)
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @Column(name = "URL", length = 100)
    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getUrl() {
        return url;
    }

    @Basic
    @Column(name = "LEVEL", length = 4)
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getLevel() {
        return level;
    }

    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    @Column(name = "ENABLE", length = 1)
    public Boolean getEnable() {
        return enable;
    }

    @Length(max = 10, message = "最长为10个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "ORDER", length = 10)
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
