package com.agile.common.generator;

import com.agile.common.base.Constant;
import com.agile.common.config.GeneratorConfig;
import com.agile.common.generator.model.TableModel;
import com.agile.common.properties.DruidConfigProperties;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.DataBaseUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.FreemarkerUtil;
import com.agile.common.util.ObjectUtil;
import freemarker.template.TemplateException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author mydeathtrial on 2017/4/20
 */

public class AgileGenerator {
    private static final String ENTITY_FTL = "Entity.ftl";
    private static final String SERVICE_FTL = "Service.ftl";
    private static final String FILE_EXTENSION = ".java";
    private static DataBaseUtil.DBInfo dbInfo;
    private static DruidConfigProperties druid;
    private static GeneratorProperties generator;


    /**
     * 推测生成java文件的包名
     *
     * @param url 生成目标文件存储路径
     * @return 包名
     */
    static String getPackPath(String url) {
        String javaPath = "src/main/java";
        if (!url.contains(javaPath)) {
            return null;
        }
        int endIndex = 0;
        if (url.endsWith(Constant.RegularAbout.SLASH)) {
            endIndex = 1;
        }
        String packPath = url.substring(url.indexOf(javaPath) + javaPath.length() + 1, url.length() - endIndex).replaceAll("/", ".");
        if (packPath.isEmpty()) {
            return null;
        }
        return packPath;
    }

    /**
     * 取数据库中所有表信息
     */
    private static List<Map<String, Object>> getTableInfo() {
        return DataBaseUtil.listTables(dbInfo, generator.getTableName());
    }

    /**
     * 初始化数据库信息
     */
    private static void initDBInfo() {
        dbInfo = new DataBaseUtil.DBInfo(druid);
    }

    /**
     * 初始化spring容器
     */
    private static void initSpringContext() {
        new AnnotationConfigApplicationContext(GeneratorConfig.class);
        druid = FactoryUtil.getBean(DruidConfigProperties.class);
        generator = FactoryUtil.getBean(GeneratorProperties.class);
    }

    /**
     * 统一路径中的斜杠
     *
     * @param str 路径
     * @return 处理后的合法路径
     */
    private static String parseUrl(String str) {
        String url = str.replaceAll("[\\\\]+", "/");
        if (!url.endsWith(Constant.RegularAbout.SLASH)) {
            url += Constant.RegularAbout.SLASH;
        }
        return url;
    }

    /**
     * 生成实体文件
     *
     * @param tableModel 表信息集
     */
    private static void generateEntityFile(TableModel tableModel) throws IOException, TemplateException {
        String url = parseUrl(generator.getEntityUrl());
        String fileName = tableModel.getEntityName() + FILE_EXTENSION;
        tableModel.setEntityPackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(ENTITY_FTL, url, fileName, tableModel, false);
    }

    /**
     * 生成service文件
     *
     * @param tableModel 表信息集
     */
    private static void generateServiceFile(TableModel tableModel) throws IOException, TemplateException {
        String url = parseUrl(generator.getServiceUrl());
        String fileName = tableModel.getServiceName() + FILE_EXTENSION;
        tableModel.setServicePackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(SERVICE_FTL, url, fileName, tableModel, false);
    }

    /**
     * 初始化环境
     */
    static void init() {
        initSpringContext();
        initDBInfo();
    }

    /**
     * 生成器
     * @param type 生成文件类型
     */
    static void generator(TYPE type) throws IOException, TemplateException {
        for (Map<String, Object> table : getTableInfo()) {
            TableModel.setDbInfo(dbInfo);
            TableModel tableModel = ObjectUtil.getObjectFromMap(TableModel.class, table);
            switch (type) {
                case ENTITY:
                    generateEntityFile(tableModel);
                    break;
                case SERVICE:
                    generateServiceFile(tableModel);
                    break;
                default:
                    generateEntityFile(tableModel);
                    generateServiceFile(tableModel);
            }
        }
    }

    /**
     * 生成文件类型
     */
    enum TYPE {
        /**
         * 实体
         */
        ENTITY,
        /**
         * service
         */
        SERVICE,
        /**
         * 全生成
         */
        DEFAULT
    }

    public static void main(String[] args) {
        try {
            init();
            generator(TYPE.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
