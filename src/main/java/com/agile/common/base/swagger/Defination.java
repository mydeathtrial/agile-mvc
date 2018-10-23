package com.agile.common.base.swagger;

import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/4
 */
public class Defination {
    private String type = "object";
    private Map<String,Property> properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
