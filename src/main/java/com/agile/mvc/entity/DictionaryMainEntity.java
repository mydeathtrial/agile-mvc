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
import org.apache.ibatis.annotations.Insert;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Id;

/**
 * 描述：[系统管理]字典表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "dictionary_main")
@Remark("[系统管理]字典表")
public class DictionaryMainEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String dictionaryMainId;
    @Remark("字典编码")
    private String code;
    @Remark("字典名称")
    private String name;

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Column(name = "dictionary_main_id", nullable = false, length = 18)
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    @Column(name = "code", nullable = false, length = 8)
    @Basic
    @NotBlank(message = "字典编码不能为空", groups = {Insert.class, Update.class})
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getCode() {
        return code;
    }

    @Column(name = "name", length = 64)
    @Length(max = 64, message = "最长为64个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getName() {
        return name;
    }


    @Override
    public DictionaryMainEntity clone() {
        try {
            return (DictionaryMainEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
