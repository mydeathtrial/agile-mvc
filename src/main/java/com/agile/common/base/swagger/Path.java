package com.agile.common.base.swagger;

import com.agile.common.annotation.Remark;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/9/22
 */
public class Path {
    private static String formData = "multipart/form-data";
    private static String formUrlencoded = "application/x-www-form-urlencoded";
    private static String json = "application/json";
    private static String xml = "application/xml";
    private List<String> tags;
    private String summary;
    private String operationId;
    private String[] consumes = new String[]{formData,formUrlencoded};
    private String[] produces = new String[]{json,xml};
    private List<Param> parameters;
    private Map<String, Map<String,String>> responses;
    private String description;

    public Path(Map.Entry<String,Object> entity,List<Param> parameters, Map<String, Map<String,String>> responses, String description) {
        setTags(entity.getKey());
        setSummary(description);
        this.parameters = parameters;
        this.responses = responses;
        this.description = description;
    }

    public static String setDescription(Class<?> clazz){
        Remark remark = AnnotationUtils.findAnnotation(clazz,Remark.class);
        if(remark == null) return "";
        return remark.value();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(String... tag) {
        if(this.tags==null){
            this.tags = new ArrayList<>();
        }
        this.tags.addAll(Arrays.asList(tag));
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String[] getConsumes() {
        return consumes;
    }

    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public List<Param> getParameters() {
        return parameters;
    }

    public void setParameters(List<Param> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Map<String, String>> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, Map<String, String>> responses) {
        this.responses = responses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
