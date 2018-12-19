package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "dictionary_main",  catalog = "agile_db")
@Remark("[系统管理]字典表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DictionaryMainEntity implements Serializable,Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String dictionaryMainId ;
    @Remark("字典编码")
    private String code ;
    @Remark("字典名称")
    private String name ;

    //无参构造器
    public DictionaryMainEntity(){}

    //有参构造器
    public DictionaryMainEntity(String dictionaryMainId,String code,String name){
        this.dictionaryMainId = dictionaryMainId;
        this.code = code;
        this.name = name;
    }

    @Id
    @Column(name = "dictionary_main_id" , nullable = false  )
    public String getDictionaryMainId() {
        return dictionaryMainId;
    }

    public void setDictionaryMainId(String dictionaryMainId) {
        this.dictionaryMainId = dictionaryMainId;
    }

    @Basic
    @Column(name = "code" , nullable = false  )
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name" , nullable = false  )
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DictionaryMainEntity)) {
            return false;
        }
        DictionaryMainEntity that = (DictionaryMainEntity) object;
        return Objects.equals(getDictionaryMainId(), that.getDictionaryMainId()) &&
            Objects.equals(getCode(), that.getCode()) &&
            Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDictionaryMainId(), getCode(), getName());
    }

    @Override
    public String toString() {
        return "DictionaryMainEntity{" +
        "dictionaryMainId='" + dictionaryMainId + '\'' +
        ",code='" + code + '\'' +
        ",name='" + name + '\'' +
        '}';
    }

    private DictionaryMainEntity(Builder builder){
        this.dictionaryMainId = builder.dictionaryMainId;
        this.code = builder.code;
        this.name = builder.name;
    }

    public static class Builder{
        private String dictionaryMainId ;
        private String code ;
        private String name ;

        public Builder setDictionaryMainId(String dictionaryMainId) {
            this.dictionaryMainId = dictionaryMainId;
            return this;
        }
        public Builder setCode(String code) {
            this.code = code;
            return this;
        }
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public DictionaryMainEntity build(){
            return new DictionaryMainEntity(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public DictionaryMainEntity clone() {
        try {
            return (DictionaryMainEntity)super.clone();
        }catch (CloneNotSupportedException e){
            return null;
        }

    }
}
