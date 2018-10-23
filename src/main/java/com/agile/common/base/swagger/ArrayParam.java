package com.agile.common.base.swagger;

import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/5
 */
public class ArrayParam extends Param {
    private Map<String,String> items;

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public ArrayParam(String name, String in, String description, boolean required, String type, Map<String, String> items) {
        super(name, in, description, required, type);
        this.items = items;
    }
}
