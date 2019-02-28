package com.agile.common.generator.model;

import com.agile.common.annotation.Remark;
import com.agile.common.base.Constant;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.NumberUtil;
import com.agile.common.util.StringUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/11 14:18
 * 描述： TODO
 * @since 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class ColumnModel {
    private String tableCat;
    private String bufferLength;
    private String tableName;
    private String columnDef;
    private String scopeCatalog;
    private String tableSchem;
    private String columnName;
    private String remarks;
    private String numPrecRadix;
    private String isAutoincrement;
    private String sqlDataType;
    private String scopeSchema;
    private String isPrimaryKey;
    private String dataType;
    private int columnSize;
    private String scopeTable;
    private String isNullable;
    private String nullable;
    private int decimalDigits;
    private String sqlDatetimeSub;
    private String isGeneratedcolumn;
    private String charOctetLength;
    private String ordinalPosition;
    private String sourceDataType;
    private String typeName;

    private String javaName;
    private String getMethod;
    private String setMethod;
    private Class javaType;
    private String javaTypeName;
    private String javaSimpleTypeName;
    private String defValue;
    private Set<Class> imports = new HashSet<>();
    private Set<String> annotations = new HashSet<>();
    private GeneratorProperties properties = FactoryUtil.getBean(GeneratorProperties.class);

    public void build() {
        StringBuilder temp = new StringBuilder();
        temp.append("name = \"").append(columnName).append("\"");
        if ("0".equals(nullable)) {
            temp.append(", nullable = false");
            if (javaType == String.class) {
                if (Boolean.valueOf(isPrimaryKey)) {
                    if (javaType == String.class) {
                        setAnnotation("@NotBlank(message = \"唯一标识不能为空\", groups = {Update.class, Delete.class})");
                        setImport(Update.class, NotBlank.class, Delete.class);
                    }
                } else {
                    setAnnotation(String.format("@NotBlank(message = \"%s不能为空\", groups = {Insert.class, Update.class})", remarks));
                    setImport(Insert.class, NotBlank.class, Update.class);
                }
            } else {
                if (Boolean.valueOf(isPrimaryKey)) {
                    if (javaType == String.class) {
                        setAnnotation("@NotNull(message = \"唯一标识不能为空\", groups = {Update.class, Delete.class})");
                        setImport(Delete.class, NotNull.class, Update.class);
                    }
                } else {
                    setAnnotation(String.format("@NotNull(message = \"%s不能为空\", groups = {Insert.class, Update.class})", remarks));
                    setImport(Insert.class, Update.class, NotNull.class);
                }
            }
        }
        if (!StringUtil.isEmpty(columnDef)) {
            temp.append(", columnDefinition = \"").append(String.format("%s default %s", typeName, columnDef)).append("\"");
        }
        if (columnSize > 0) {
            temp.append(", length = ").append(columnSize);
            if (javaType == String.class) {
                setImport(Length.class, Insert.class, Update.class);
                setAnnotation(String.format("@Length(max = %s, message = \"最长为%s个字符\", groups = {Insert.class, Update.class})", columnSize, columnSize));
            } else if (javaType == int.class || javaType == Integer.class) {
                setImport(Max.class, Min.class);
                setAnnotation(String.format("@Max(value = %s, groups = {Insert.class, Update.class})", Integer.MAX_VALUE));
                setAnnotation(String.format("@Min(value = %s, groups = {Insert.class, Update.class})", Constant.NumberAbout.ZERO));
            } else if (javaType == long.class || javaType == Long.class) {
                setImport(DecimalMax.class, DecimalMin.class);
                setAnnotation(String.format("@DecimalMax(value = \"%s\", groups = {Insert.class, Update.class})", Long.MAX_VALUE));
                setAnnotation(String.format("@DecimalMin(value = \"%s\", groups = {Insert.class, Update.class})", Constant.NumberAbout.ZERO));
            }
        }
        if ("creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName)) {
            temp.append(", updatable = false");
            setImport(Past.class);
            setAnnotation("@Past");
        }
        setAnnotation(String.format("@Column(%s)", temp));

        if (Boolean.valueOf(isPrimaryKey)) {
            setAnnotation("@Id");
        } else {
            if ("byte[]".equals(javaTypeName) || "java.sql.Blob".equals(javaTypeName) || "java.sql.Clob".equals(javaTypeName)) {
                if ("java.sql.Blob".equals(javaTypeName) || "java.sql.Clob".equals(javaTypeName)) {
                    setAnnotation("@Lob");
                    setImport(Lob.class, FetchType.class);
                }
                setAnnotation("@Basic(fetch = FetchType.LAZY)");
            } else {
                setAnnotation("@Basic");
            }
        }
    }


    public void setColumnName(String columnName) {
        this.columnName = columnName;
        if (properties.isSensitive()) {
            this.javaName = StringUtil.toLowerName(columnName);
        } else {
            this.javaName = StringUtil.toLowerName(columnName.toLowerCase());
        }

        javaName = javaName.replaceAll(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.NULL);

        if ("updateTime".equals(javaName) || "updateDate".equals(javaName)) {
            setAnnotation("@Temporal(TemporalType.TIMESTAMP)");
            setAnnotation("@UpdateTimestamp");
            setImport(UpdateTimestamp.class, Temporal.class, TemporalType.class);
        }

        if ("creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName)) {
            setAnnotation("@Temporal(TemporalType.TIMESTAMP)");
            setAnnotation("@CreationTimestamp");
            setImport(CreationTimestamp.class, Temporal.class, TemporalType.class);
        }
        setMethod(javaName);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        if ("TIMESTAMP".equals(typeName) || "DATE".equals(typeName) || "TIME".equals(typeName)) {
            setAnnotation(String.format("@Temporal(TemporalType.%s)", typeName));
            setImport(Temporal.class, TemporalType.class);
        }

        this.javaTypeName = properties.getJavaType(typeName.split("[\\s]+")[0].toLowerCase());

        try {
            this.javaType = Class.forName(javaTypeName);
            this.javaSimpleTypeName = javaType.getSimpleName();
            setImport(javaType);
        } catch (ClassNotFoundException ignored) {

        }
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        setImport(Id.class);
    }

    private void setMethod(String name) {
        if (boolean.class == javaType) {
            this.getMethod = "is" + StringUtil.toUpperName(name);
        } else {
            this.getMethod = "get" + StringUtil.toUpperName(name);
        }
        this.setMethod = "set" + StringUtil.toUpperName(name);

    }

    public void setIsAutoincrement(String isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
        if ("YES".equals(isAutoincrement)) {
            setImport(GenerationType.class, GeneratedValue.class);
            setAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
        }
    }

    public void setColumnDef(String columnDef) {
        this.columnDef = columnDef;
        if (columnDef == null || "null".equals(columnDef.toLowerCase())) {
            return;
        }

        if (Double.class == javaType) {
            defValue = NumberUtil.isNumber(columnDef) ? Double.valueOf(columnDef).toString() : null;
        } else if (String.class == javaType || Char.class == javaType) {
            defValue = String.format("\"%s\"", columnDef.replaceAll(Constant.RegularAbout.UP_COMMA, ""));
        } else if ("CURRENT_TIMESTAMP".equals(columnDef)) {
            if (Date.class == javaType || java.sql.Date.class == javaType) {
                defValue = "new Date()";
            } else if (Time.class == javaType) {
                defValue = "new Time(System.currentTimeMillis())";
            } else if (Timestamp.class == javaType) {
                defValue = "new Timestamp(System.currentTimeMillis())";
            } else if (long.class == javaType) {
                defValue = "System.currentTimeMillis()";
            }
        }
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks.replaceAll("[\\s]+", " ");
        if (!StringUtil.isEmpty(remarks)) {
            setImport(Remark.class);
        }
    }

    public void setImport(Class... classes) {
        for (Class clazz : classes) {
            if (clazz.getPackage().getName().startsWith("java.lang")) {
                continue;
            }
            this.imports.add(clazz);
        }
    }

    private void setAnnotation(String annotation) {
        this.annotations.add(annotation);
    }

}
