package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import com.agile.common.base.Constant;
import com.agile.common.util.CollectionsUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：[系统管理]模块
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
@Table(name = "sys_modules")
@Remark("[系统管理]模块")
public class SysModulesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysModulesId;
    @Remark("模块上级")
    @Builder.Default
    private String parentId = ROOT_ID;
    @Remark("模块名称")
    private String name;
    @Remark("模块说明")
    private String desc;
    @Remark("模块地址")
    private String url;
    @Remark("是否可用")
    private Boolean enable;
    @Remark("优先级")
    private Integer order;
    @Transient
    private List<SysModulesEntity> children = new ArrayList<>();

    /**
     * 根节点父级主键标识
     */
    private static final String ROOT_ID = "root";
    /**
     * 排序字段
     */
    private static final String SORT_COLUMN = "order";

    /**
     * 构建树形结构
     *
     * @param list 构建源数据
     * @return 树形结构数据集
     */
    public static List<Map<String, Object>> createTree(List<SysModulesEntity> list) {
        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysModulesEntity entity : list) {
            if (ROOT_ID.equals(entity.getParentId())) {
                roots.add(createMap(entity, list));
            }
        }
        CollectionsUtil.sort(roots, SORT_COLUMN);
        return roots;
    }

    /**
     * 获取子菜单
     *
     * @param parent 父节点
     * @param list   全部节点
     * @return 子菜单
     */
    public static List<Map<String, Object>> createChildren(SysModulesEntity parent, List<SysModulesEntity> list) {
        List<Map<String, Object>> children = new ArrayList<>();
        for (SysModulesEntity entity : list) {
            if (parent.getSysModulesId().equals(entity.getParentId())) {
                children.add(createMap(entity, list));
            }
        }
        CollectionsUtil.sort(children, SORT_COLUMN);
        return children;
    }

    private static Map<String, Object> createMap(SysModulesEntity entity, List<SysModulesEntity> list) {
        Map<String, Object> map = new HashMap<>(Constant.NumberAbout.THREE);
        map.put("name", entity.getUrl());
        map.put("order", entity.getOrder());
        map.put("meta", new HashMap<String, Object>(Constant.NumberAbout.ONE) {{
            put("title", entity.getName());
        }});
        map.put("children", createChildren(entity, list));
        return map;
    }

    @Column(name = "SYS_MODULES_ID", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysModulesId() {
        return sysModulesId;
    }

    @Column(name = "PARENT_ID", columnDefinition = "VARCHAR default root", length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @NotBlank(message = "模块名称不能为空", groups = {Insert.class, Update.class})
    @Length(max = 24, message = "最长为24个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "`NAME`", nullable = false, length = 24)
    public String getName() {
        return name;
    }

    @Length(max = 200, message = "最长为200个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "`DESC`", length = 200)
    public String getDesc() {
        return desc;
    }

    @Column(name = "URL", length = 100)
    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getUrl() {
        return url;
    }

    @Column(name = "`ENABLE`", columnDefinition = "BIT default 0", length = 1)
    @Basic
    public Boolean getEnable() {
        return enable;
    }

    @Min(value = 0, groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "`ORDER`", length = 10)
    @Max(value = 2147483647, groups = {Insert.class, Update.class})
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
