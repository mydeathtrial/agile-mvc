package com.agile.common.view;

import com.agile.common.util.FileUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.ViewUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/8/1
 */
public class JsonView extends MappingJackson2JsonView {
    public JsonView() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(PropertiesUtil.getProperty("spring.mvc.date-format")));
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeNull();
            }
        });
        this.setPrettyPrint(true);
        this.setObjectMapper(objectMapper);
        this.setBeanName("jsonView");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Object> files = ViewUtil.extractFiles(model);
        if (files.size() > 0) {
            FileUtil.downloadFile(files, request, response);
        } else {
            super.renderMergedOutputModel(model, request, response);
        }
    }
}
