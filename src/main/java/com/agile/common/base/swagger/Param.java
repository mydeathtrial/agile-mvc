package com.agile.common.base.swagger;

/**
 * Created by 佟盟 on 2018/10/4
 */
public class Param {
    private String name;
    private String in;
    private String description;
    private boolean required;
    private String type;

    public Param(String name, String in, String description, boolean required, String type) {
        this.name = name;
        this.in = in;
        this.description = description;
        this.required = required;
        this.type = type;
    }

    public Param() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
