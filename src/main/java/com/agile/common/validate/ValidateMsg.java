package com.agile.common.validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author 佟盟 on 2018/11/15
 */
@Data
public class ValidateMsg {
    private String message;
    private boolean state = true;
    private String item;
    private Object itemValue;

    public ValidateMsg(String msg, boolean state, String paramKey, Object paramValue) {
        this.message = msg;
        this.state = state;
        this.item = paramKey;
        this.itemValue = paramValue;
    }

    public ValidateMsg(String paramKey, Object paramValue) {
        this.item = paramKey;
        this.itemValue = paramValue;
    }

    @JsonIgnore
    public boolean isState() {
        return state;
    }

    public void addMessage(String msg) {
        message = String.format("%s;%s", message, msg);
    }
}
