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

    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Column(name = "dictionary_data_id", nullable = false, length = 18)
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getDictionaryDataId() {
        return dictionaryDataId;
    }

    @Basic
    @Column(name = "dictionary_main_id", nullable = false, length = 18)
    @NotEmpty(message = "字典主表主键不能为空", groups = {Insert.class, Update.class})
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    @Basic
    @Column(name = "parent_id", length = 18)
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @Length(max = 50, message = "最长为50个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "key", nullable = false, length = 50)
    @NotEmpty(message = "显示名称不能为空", groups = {Insert.class, Update.class})
    public String getKey() {
        return key;
    }

    @NotEmpty(message = "代表值不能为空", groups = {Insert.class, Update.class})
    @Length(max = 50, message = "最长为50个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "value", nullable = false, length = 50)
    public String getValue() {
        return value;
    }

    @Basic
    @Length(max = 1, message = "最长为1个字符", groups = {Insert.class, Update.class})
    @Column(name = "is_fixed", nullable = false, length = 1)
    @NotEmpty(message = "字典值是否固定不能为空", groups = {Insert.class, Update.class})
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
