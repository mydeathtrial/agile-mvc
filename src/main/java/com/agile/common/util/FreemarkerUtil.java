package com.agile.common.util;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.generator.AgileGenerator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟 on 2018/6/29
 */
public class FreemarkerUtil {
    private static Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    private static String encoder = StandardCharsets.UTF_8.name();

    static {
        initFreemarker();
    }

    private static void initFreemarker() {
        try {
            cfg.setClassForTemplateLoading(AgileGenerator.class, "/com/agile/common/generator/template");
            cfg.setDefaultEncoding(encoder);
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Template getTemplate(String name) {
        try {
            return cfg.getTemplate(name, encoder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成器引擎
     *
     * @param templateURI 模板地址
     * @param fileName    文件名
     * @param data        数据
     */
    public static void generatorProxy(String templateURI, String directory, String fileName, Object data, boolean append) throws IOException, TemplateException {
        Template template = getTemplate(templateURI);
        File serviceFileDir = new File(directory);
        if (!serviceFileDir.exists()) {
            boolean f = serviceFileDir.mkdirs();
            if (!f) {
                LoggerFactory.COMMON_LOG.error(String.format("无法创建代码生成路径：%s", directory));
                return;
            }
        }
        File serviceFile = new File(serviceFileDir.getPath() + "\\" + fileName);
        BufferedWriter serviceFileBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile, append), encoder));
        assert template != null;
        template.process(data, serviceFileBw);
        serviceFileBw.flush();
        serviceFileBw.close();
    }
}
