package com.agile.common.generator;

import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.DataBaseUtil;
import com.agile.common.util.FreemarkerUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/6/29
 */
public class AgileSwaggerGenerator {
    public static void main(String[] args) {
        try {
            List<Map<String, Object>> tables = DataBaseUtil.listTables(PropertiesUtil.getProperty("agile.druid.type"), PropertiesUtil.getProperty("agile.druid.data_base_ip"), PropertiesUtil.getProperty("agile.druid.data_base_post"), PropertiesUtil.getProperty("agile.druid.data_base_name"), PropertiesUtil.getProperty("agile.druid.data_base_username"), PropertiesUtil.getProperty("agile.druid.data_base_password"), PropertiesUtil.getProperty("agile.generator.table_name"));

            List<Map<String, Object>> tableInfoList = new ArrayList<>();
            //模板配置
            for (Map<String, Object> tableData:tables) {
                tableInfoList.add(AgileGenerator.tableHandle(tableData));
            }

            String pattern = "classpath*:com/agile/mvc/service/**/*.class";
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(new PathMatchingResourcePatternResolver());
            for(Resource resource:resources){
                String className = readerFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);
                if(clazz.getSuperclass() == BusinessService.class){
                    System.out.println(className);
                }
            }
            //API生成器
            FreemarkerUtil.generatorProxy("API.ftl", PropertiesUtil.getProperty("agile.generator.api_url"),"api.json",new HashMap<String,Object>(){{put("list",tableInfoList);}},false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
