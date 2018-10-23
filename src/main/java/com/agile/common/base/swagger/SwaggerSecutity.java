package com.agile.common.base.swagger;

/**
 * Created by 佟盟 on 2018/10/4
 */
public class SwaggerSecutity {
    private PetstoreAuth petstore_auth;
    private ApiKey api_key;

    public SwaggerSecutity(PetstoreAuth petstore_auth, ApiKey api_key) {
        this.petstore_auth = petstore_auth;
        this.api_key = api_key;
    }

    public PetstoreAuth getPetstore_auth() {
        return petstore_auth;
    }

    public void setPetstore_auth(PetstoreAuth petstore_auth) {
        this.petstore_auth = petstore_auth;
    }

    public ApiKey getApi_key() {
        return api_key;
    }

    public void setApi_key(ApiKey api_key) {
        this.api_key = api_key;
    }
}
