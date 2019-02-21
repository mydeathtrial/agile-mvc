package com.agile.common.generator.model;

import com.agile.common.annotation.Remark;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.DataBaseUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class TableModel {
    private String tableCat;
    private String tableName;
    private String selfReferencingColName;
    private String tableSchem;
    private String typeSchem;
    private String typeCat;
    private String tableType;
    private String remarks;
    private String refGeneration;
    private String typeName;

    private List<ColumnModel> columns = new ArrayList<>();
    private Set<String> imports = new HashSet<>();
    private String serviceName;
    private String entityName;
    private String javaName;
    private String servicePackageName;
    private String entityPackageName;

    private GeneratorProperties properties = FactoryUtil.getBean(GeneratorProperties.class);
    private static DataBaseUtil.DBInfo dbInfo;

    public void setColumn(ColumnModel columns) {
        this.columns.add(columns);
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks.replaceAll("[\\s]+", " ");
        if (!StringUtil.isEmpty(remarks)) {
            setImport(Remark.class);
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.javaName = StringUtil.toUpperName(tableName);

        List<Map<String, Object>> columnInfos = DataBaseUtil.listColumns(dbInfo, tableName);
        for (Map<String, Object> column : columnInfos) {
            ColumnModel columnModel = ObjectUtil.getObjectFromMap(ColumnModel.class, column);
            columnModel.build();
            setImport(columnModel.getImports());
            setColumn(columnModel);
        }

        this.serviceName = properties.getServicePrefix() + javaName + properties.getServiceSuffix();
        this.entityName = properties.getEntityPrefix() + javaName + properties.getEntitySuffix();
    }

    public void setImport(Set<Class> classes) {
        if (classes == null) {
            return;
        }
        for (Class clazz : classes) {
            setImport(clazz);
        }
    }

    public void setImport(Class clazz) {
        if (clazz.getPackage().getName().startsWith("java.lang")) {
            return;
        }
        this.imports.add(String.format("%s.%s", clazz.getPackage().getName(), clazz.getSimpleName()));
    }

    public static void setDbInfo(DataBaseUtil.DBInfo dbInfo) {
        TableModel.dbInfo = dbInfo;
    }
}
