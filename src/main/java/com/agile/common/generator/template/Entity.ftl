package ${entityPackage};

import com.agile.common.annotation.Remark;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;
<#list importList as import>
import ${import}
</#list>

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "${tableName}", <#if schemaName??>schema = "${schemaName}",</#if> catalog = "${catalogName}")
@Remark("${tableComment}")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ${entityClassName} implements Serializable,Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
<#list columnList as property>
    @Remark("${property.remarks}")
    private ${property.propertyType} ${property.propertyName};
</#list>

    //无参构造器
    public ${entityClassName}(){}

    //有参构造器
    public ${entityClassName}(<#list columnList as property>${property.propertyType} ${property.propertyName}<#if property_has_next>,</#if></#list>){
        <#list columnList as property>
        this.${property.propertyName} = ${property.propertyName};
        </#list>
    }

<#list columnList as property>
    <#if property.isPrimaryKey == "true">
    @Id
    <#elseif property.columnType == "blob" || property.columnType == "text" || property.columnType == "clob" >
    @Basic(fetch=FetchType.LAZY)
    <#else>
    @Basic
    </#if>
    <#if property.isAutoincrement == "YES">
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    </#if>
    <#if property.columnType == "blob" || property.columnType == "clob" >
    @Lob
    </#if>
    @Column(name = "${property.columnName}" <#if property.nullable == "false">, nullable = ${property.nullable} </#if>)
    public ${property.propertyType} ${property.getMethod}() {
        return ${property.propertyName};
    }

    public void ${property.setMethod}(${property.propertyType} ${property.propertyName}) {
        this.${property.propertyName} = ${property.propertyName};
    }

</#list>

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ${entityClassName})) return false;
        ${entityClassName} that = (${entityClassName}) object;
        return <#list columnList as property>Objects.equals(${property.getMethod}(), that.${property.getMethod}())<#if property_has_next> &&
            <#else >;
            </#if></#list>
    }

    @Override
    public int hashCode() {
        return Objects.hash(<#list columnList as property>${property.getMethod}()<#if property_has_next>, </#if></#list>);
    }

    @Override
    public String toString() {
        return "${entityClassName}{" +
        <#list columnList as property>
        <#if property.propertyType == "Integer" || property.propertyType == "Double" || property.propertyType == "Float" || property.propertyType == "Long" || property.propertyType == "Short" || property.propertyType == "Date" || property.propertyType == "Timestamp" || property.propertyType == "Clob" || property.propertyType == "Blob" || property.propertyType == "int" || property.propertyType == "double" || property.propertyType == "float" || property.propertyType == "long" || property.propertyType == "short" || property.propertyType == "Boolean" || property.propertyType == "boolean">
        "<#if property_index != 0>,</#if>${property.propertyName}=" + ${property.propertyName} +
        <#else>
        "<#if property_index != 0>,</#if>${property.propertyName}='" + ${property.propertyName} + '\'' +
        </#if>
        </#list>
        '}';
    }

    private ${entityClassName}(Builder builder){
        <#list columnList as property>
        this.${property.propertyName} = builder.${property.propertyName};
        </#list>
    }

    public static class Builder{
        <#list columnList as property>
        private ${property.propertyType} ${property.propertyName};
        </#list>

        <#list columnList as property>
        public Builder ${property.setMethod}(${property.propertyType} ${property.propertyName}) {
            this.${property.propertyName} = ${property.propertyName};
            return this;
        }
        </#list>
        public ${entityClassName} build(){
            return new ${entityClassName}(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public ${entityClassName} clone() {
        try {
            return (${entityClassName})super.clone();
        }catch (CloneNotSupportedException e){
            return null;
        }

    }
}
