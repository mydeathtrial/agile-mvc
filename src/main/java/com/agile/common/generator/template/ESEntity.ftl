package ${package};

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
<#list importList as import>
    import ${import}
</#list>

@Document(indexName = "${dbName}", type = "${tableName}")
public class ${className} implements Serializable,Cloneable {

//序列
private static final long serialVersionUID = 1L;
@Id
@Field(type = FieldType.keyword)
private String id;
<#list params as property>
    @Field(type = FieldType.${property.fieldType})
    private String ${property.paramName};
</#list>

//无参构造器
public ${className}(){}

//有参构造器
public ${className}(<#list params as property>${property.paramType} ${property.paramName}<#if property_has_next>,</#if></#list>){
<#list params as property>
    this.${property.paramName} = ${property.paramName};
</#list>
}

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}
<#list params as property>
    public ${property.paramType}  ${property.getMethod}() {
    return ${property.paramName};
    }

    public void ${property.setMethod}(<#if property.paramType == "Integer" >int <#elseif property.paramType == "Long" >long <#else>${property.paramType} </#if>${property.paramName}) {
    this.${property.paramName} = ${property.paramName};
    }

</#list>

@Override
public boolean equals(Object object) {
if (this == object) return true;
if (!(object instanceof ${className})) return false;
${className} that = (${className}) object;
return Objects.equals(getId(), that.getId()) &&
<#list params as property>Objects.equals(${property.getMethod}(), that.${property.getMethod}())<#if property_has_next> &&
<#else >;
</#if></#list>
}

@Override
public int hashCode() {
return Objects.hash(getId(),<#list params as property>${property.getMethod}()<#if property_has_next>, </#if></#list>);
}

@Override
public String toString() {
return "${className}{" +
<#list params as property>
    <#if property.paramType == "Integer" || property.paramType == "Double" || property.paramType == "Float" || property.paramType == "Long" || property.paramType == "Short" || property.paramType == "Date" || property.paramType == "Timestamp" || property.paramType == "Clob" || property.paramType == "Blob" || property.paramType == "int" || property.paramType == "double" || property.paramType == "float" || property.paramType == "long" || property.paramType == "short" || property.paramType == "Boolean" || property.paramType == "boolean">
        "<#if property_index != 0>,</#if>${property.paramName}=" + ${property.paramName} +
    <#else>
        "<#if property_index != 0>,</#if>${property.paramName}='" + ${property.paramName} + '\'' +
    </#if>
</#list>
",id='" + id + '\'' +
'}';
}

private ${className}(Builder builder){
this.id = builder.id;
<#list params as property>
    this.${property.paramName} = builder.${property.paramName};
</#list>
}

public static class Builder{
private String id;
<#list params as property>
    private ${property.paramType} ${property.paramName};
</#list>

public Builder setId(String id) {
this.id = id;
return this;
}
<#list params as property>
    public Builder ${property.setMethod}(<#if property.paramType == "Integer" >int <#elseif property.paramType == "Long" >long <#else>${property.paramType} </#if>${property.paramName}) {
    this.${property.paramName} = ${property.paramName};
    return this;
    }
</#list>
public ${className} build(){
return new ${className}(this);
}
}

public static Builder builder(){
return new Builder();
}

@Override
public ${className} clone() {
try {
return (${className})super.clone();
}catch (CloneNotSupportedException e){
return null;
}
}
}
