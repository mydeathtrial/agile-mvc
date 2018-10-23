package com.agile.common.base.swagger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/9/22
 */
public class SwaggerInfo {
    public enum RequestMethod{
        get,post,put,delete
    }
    private String swagger = "2.0";
    private Info info;
    private List<Tag> tags;
    private String[] schemes = new String[]{Scheme.http.name(),Scheme.https.name()};
    private Map<String, HashMap<String,Path>>  paths;
    private enum Scheme{
        http,https
    }
    private SwaggerSecutity securityDefinitions;
    private Map<String,Defination> definitions;
    private ExternalDocs externalDocs;

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String[] getSchemes() {
        return schemes;
    }

    public void setSchemes(String[] schemes) {
        this.schemes = schemes;
    }

    public Map<String, HashMap<String,Path>>  getPaths() {
        return paths;
    }

    public void setPaths(Map<String, HashMap<String,Path>>  paths) {
        this.paths = paths;
    }

    public SwaggerSecutity getSecurityDefinitions() {
        return securityDefinitions;
    }

    public void setSecurityDefinitions(SwaggerSecutity securityDefinitions) {
        this.securityDefinitions = securityDefinitions;
    }

    public Map<String, Defination> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, Defination> definitions) {
        this.definitions = definitions;
    }

    public ExternalDocs getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDocs externalDocs) {
        this.externalDocs = externalDocs;
    }
}
