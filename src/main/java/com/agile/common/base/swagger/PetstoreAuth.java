package com.agile.common.base.swagger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/5
 */
public class PetstoreAuth {
    private String type = "oauth2";
    private String authorizationUrl;
    private String flow = "implicit";
    private Map<String,String> scopes;

    public PetstoreAuth(String type, String authorizationUrl, String flow, Map<String, String> scopes) {
        this.type = type;
        this.authorizationUrl = authorizationUrl;
        this.flow = flow;
        this.scopes = scopes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public Map<String, String> getScopes() {
        return scopes;
    }

    public void setScopes(Map<String, String> scopes) {
        this.scopes = scopes;
    }
}
