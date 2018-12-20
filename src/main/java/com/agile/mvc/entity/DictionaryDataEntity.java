package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 描述：[系统管理]字典数据表
 * @author agile gennerator
 */
@Entity
@Table(name = "dictionary_data")
@Remark("[系统管理]字典数据表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DictionaryDataEntity implements Serializable, Cloneable {

    /**
     * 序列化参数
     */
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

    /**
     * 无参构造器
     */
    public DictionaryDataEntity() { }

    /**
     * 带参构造器
     */
    public DictionaryDataEntity(String dictionaryDataId, String dictionaryMainId, String parentId, String key, String value, Boolean isFixed) {
        this.dictionaryDataId = dictionaryDataId;
        this.dictionaryMainId = dictionaryMainId;
        this.parentId = parentId;
        this.key = key;
        this.value = value;
        this.isFixed = isFixed;
    }

    @Id
    @Column(name = "dictionary_data_id", nullable = false)
    public String getDictionaryDataId() {
        return dictionaryDataId;
    }

    public void setDictionaryDataId(String dictionaryDataId) {
    this.dictionaryDataId = dictionaryDataId;
    }

    @Basic
    @Column(name = "dictionary_main_id", nullable = false)
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    public void setDictionaryMainId(String dictionaryMainId) {
    this.dictionaryMainId = dictionaryMainId;
    }

    @Basic
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
    this.parentId = parentId;
    }

    @Basic
    @Column(name = "key", nullable = false)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
    this.key = key;
    }

    @Basic
    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
    this.value = value;
    }

    @Basic
    @Column(name = "is_fixed", nullable = false)
    public Boolean getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Boolean isFixed) {
    this.isFixed = isFixed;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DictionaryDataEntity)) {
            return false;
        }
        DictionaryDataEntity that = (DictionaryDataEntity) object;
        return Objects.equals(getDictionaryDataId(), that.getDictionaryDataId())
        && Objects.equals(getDictionaryMainId(), that.getDictionaryMainId())
        && Objects.equals(getParentId(), that.getParentId())
        && Objects.equals(getKey(), that.getKey())
        && Objects.equals(getValue(), that.getValue())
        && Objects.equals(getIsFixed(), that.getIsFixed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDictionaryDataId(), getDictionaryMainId(), getParentId(), getKey(), getValue(), getIsFixed());
    }

    @Override
    public String toString() {
    return "DictionaryDataEntity{"
            + "dictionaryDataId ='" + dictionaryDataId + '\''
            + ",dictionaryMainId ='" + dictionaryMainId + '\''
            + ",parentId ='" + parentId + '\''
            + ",key ='" + key + '\''
            + ",value ='" + value + '\''
            + ",isFixed =" + isFixed + '}';
    }

    private DictionaryDataEntity(Builder builder) {
        this.dictionaryDataId = builder.dictionaryDataId;
        this.dictionaryMainId = builder.dictionaryMainId;
        this.parentId = builder.parentId;
        this.key = builder.key;
        this.value = builder.value;
        this.isFixed = builder.isFixed;
    }

    /**
     * 建造者
     */
    public static class Builder {
        private String dictionaryDataId;
        private String dictionaryMainId;
        private String parentId;
        private String key;
        private String value;
        private Boolean isFixed;

        public Builder setDictionaryDataId(String dictionaryDataId) {
            this.dictionaryDataId = dictionaryDataId;
            return this;
        }
        public Builder setDictionaryMainId(String dictionaryMainId) {
            this.dictionaryMainId = dictionaryMainId;
            return this;
        }
        public Builder setParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }
        public Builder setKey(String key) {
            this.key = key;
            return this;
        }
        public Builder setValue(String value) {
            this.value = value;
            return this;
        }
        public Builder setIsFixed(Boolean isFixed) {
            this.isFixed = isFixed;
            return this;
        }
        public DictionaryDataEntity build() {
            return new DictionaryDataEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
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
