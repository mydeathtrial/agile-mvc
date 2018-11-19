package com.agile.common.validate;

import com.agile.common.annotation.Validate;

import java.util.List;

/**
 * Created by 佟盟 on 2018/11/15
 */
public interface ValidateInterface {
    ValidateMsg validateParam(String key, Object value, Validate validate);
    List<ValidateMsg> validateArray(String key, String[] value,Validate validate);
}
