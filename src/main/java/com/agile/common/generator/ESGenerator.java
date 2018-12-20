package com.agile.common.generator;

import com.agile.common.util.ESUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import com.carrotsearch.hppc.ObjectContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import freemarker.template.TemplateException;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/21
 */
public class ESGenerator {
    private static Map<String, Object> tableHandle(String dbName, ObjectCursor<MappingMetaData> typeInfo) throws IOException {
        Map<String, Object> data = new HashMap<>();
        String tableName = typeInfo.value.type();
        data.put("dbName", dbName);
        data.put("tableName", tableName);
        String className = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.es_entity_prefix")) + StringUtil.toUpperName(tableName) + StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.es_entity_suffix"));
        data.put("className", className);
        Map<String, Object> sources = typeInfo.value.getSourceAsMap();
        Object properties = sources.get("properties");
        if (!(properties instanceof Map)) {
            return null;
        }

        List<Map<String, String>> params = new LinkedList<>();
        //导入集
        List<String> importList = new ArrayList<>();
        for (Map.Entry property : ((Map<String, Object>) properties).entrySet()) {

            Map<String, String> param = new HashMap<>();
            String paramName = (String) property.getKey();
            if ("id".equals(paramName)) {
                continue;
            }
            param.put("paramName", paramName);
            param.put("getMethod", "get" + StringUtil.toUpperName(paramName));
            param.put("setMethod", "set" + StringUtil.toUpperName(paramName));
            String paramType = ((Map<String, String>) (property.getValue())).get("type");

            //API导入
            if ("date".equals(paramType)) {
                importList.add("java.util.Date;");
            }
            param.put("fieldType", PropertiesUtil.getProperty("agile.elasticsearch.column_type." + paramType));
            param.put("paramType", "String");
            params.add(param);
        }
        data.put("importList", importList);
        data.put("params", params);

        return data;
    }

    public static void main(String[] args) throws IOException, TemplateException {
        String[] indexs = ESUtil.getAllIndex();
        int i = 0;
        while (i < indexs.length) {
            String dbName = indexs[i++];
            ObjectContainer<MappingMetaData> types = ESUtil.getTypeByIndex(dbName);
            Iterator<ObjectCursor<MappingMetaData>> it = types.iterator();
            while (it.hasNext()) {
                ObjectCursor<MappingMetaData> tableInfo = it.next();
                Map<String, Object> data = tableHandle(dbName, tableInfo);
                AgileGenerator.generateFile(data, "agile.generator.es_entity_url", "className", "ESEntity.ftl", "package");
            }
        }
    }
}
