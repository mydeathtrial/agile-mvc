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
 * 描述：[系统管理]字典数据表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "dictionary_data")
@Remark("[系统管理]字典数据表")
public class DictionaryDataEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String dictionaryDataId;
    @Remark("字典主表主键")
    private String dictionaryMainId;
    @Remark("父节点主键")
    private String parentId;
    @Remark("显示名称")
    private String key;
    @Remark("代表值")
    private String value;
    @Remark("字典值是否固定")
    private Boolean isFixed;

    @Column(name = "dictionary_data_id", nullable = false)
    @Id
    public String getDictionaryDataId() {
        return dictionaryDataId;
    }

    @Column(name = "dictionary_main_id", nullable = false)
    @Basic
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    @Basic
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    @Column(name = "key", nullable = false)
    @Basic
    public String getKey() {
        return key;
    }

    @Basic
    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    @Column(name = "is_fixed", nullable = false)
    @Basic
    public Boolean getIsFixed() {
        return isFixed;
    }


    @Override
    public DictionaryDataEntity clone() {
        try {
            return (DictionaryDataEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
