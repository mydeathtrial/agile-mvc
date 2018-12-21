package com.agile.common.validate;

/**
 * @author 佟盟 on 2018/11/15
 */
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Object getItemValue() {
        return itemValue;
    }

    public void setItemValue(Object itemValue) {
        this.itemValue = itemValue;
    }
}
