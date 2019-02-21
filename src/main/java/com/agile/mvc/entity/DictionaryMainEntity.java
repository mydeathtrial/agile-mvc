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

    @Id
    @Column(name = "dictionary_main_id", nullable = false, length = 18)
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    @Column(name = "code", nullable = false, length = 8)
    @Basic
    public String getCode() {
        return code;
    }

    @Column(name = "name", length = 64)
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
