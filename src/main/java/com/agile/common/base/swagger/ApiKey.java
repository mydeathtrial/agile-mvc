package com.agile.common.base.swagger;

/**
 * Created by 佟盟 on 2018/10/5
 */
public class ApiKey {
    private String type = "apiKey";
    private String name = "api_key";
    private String in = "header";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
