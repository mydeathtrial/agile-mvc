package com.agile.common.base.swagger;

import java.util.Map;

/**
 * Created by 佟盟 on 2018/9/22
 */
public class Info {
    private String version;
    private String title;
    private Map<String,Object> contact;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getContact() {
        return contact;
    }

    public void setContact(Map<String, Object> contact) {
        this.contact = contact;
    }
}
