package com.agile.common.view;

import com.agile.common.util.FileUtil;
import com.agile.common.util.ViewUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        this.setPrettyPrint(true);
        this.setObjectMapper(objectMapper);
        this.setBeanName("jsonView");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ViewUtil.Model target = ViewUtil.modelProcessing(model);
        List<Object> files = target.getFiles();
        if (files.size() > 0) {
            FileUtil.downloadFile(files, request, response);
        } else {
            super.renderMergedOutputModel(target, request, response);
        }
    }
}
