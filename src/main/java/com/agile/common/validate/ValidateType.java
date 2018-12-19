package com.agile.common.validate;

import com.agile.common.annotation.Validate;
import com.agile.common.base.Constant;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import net.sf.json.JSONNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 佟盟 on 2018/11/16
 */
public enum ValidateType implements ValidateInterface {
    NO,
    EMAIL(Constant.RegularAbout.EMAIL, "邮箱"),
    DOMAIN(Constant.RegularAbout.DOMAIN, "域名"),
    INTERNET_UTL(Constant.RegularAbout.INTERNET_UTL, "Internet地址"),
    MOBILE_PHONE(Constant.RegularAbout.MOBILE_PHONE, "手机号码"),
    PHONE(Constant.RegularAbout.PHONE, "电话号码"),
    /**
     * 国内电话号码(0511-4405222、021-87888822)
     */
    CHINA_PHONE(Constant.RegularAbout.CHINA_PHONE, "国内电话号码"),
    /**
     * 身份证号(15位、18位数字)
     */
    ID_CARD(Constant.RegularAbout.ID_CARD, " 身份证号"),
    /**
     * 短身份证号码(数字、字母x结尾)
     */
    SHORT_ID_CARD(Constant.RegularAbout.SHORT_ID_CARD, "短身份证号码"),
    /**
     * 帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)
     */
    ACCOUNT(Constant.RegularAbout.ACCOUNT, "帐号"),
    /**
     * 密码(以字母开头，长度在6~18之间，只能包含字母、数字和下划线)
     */
    PASSWORD(Constant.RegularAbout.PASSWORD, "密码"),
    /**
     * 强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)
     */
    STRONG_PASSWORD(Constant.RegularAbout.STRONG_PASSWORD, "强密码"),
    DATE_YYYY_MM_DD(Constant.RegularAbout.DATE_YYYY_MM_DD, "日期（yyyy-mm-dd）"),
    MONTH(Constant.RegularAbout.MONTH, "月份"),
    DAY(Constant.RegularAbout.DAY, "日期天"),
    MONEY(Constant.RegularAbout.MONEY, "钱"),
    XML_FILE_NAME(Constant.RegularAbout.XML_FILE_NAME, "xml文件名"),
    CHINESE_LANGUAGE(Constant.RegularAbout.CHINESE_LANGUAGE, "中文"),
    TWO_CHAR(Constant.RegularAbout.TWO_CHAR, "双字节字符"),
    QQ(Constant.RegularAbout.QQ, "QQ号"),
    MAIL_NO(Constant.RegularAbout.MAIL_NO, "中国邮政编码"),
    IP(Constant.RegularAbout.IP, "IP地址"),
    NUMBER(Constant.RegularAbout.NUMBER, "数字"),
    FLOAT(Constant.RegularAbout.FLOAT, "浮点数"),
    ENGLISH_NUMBER(Constant.RegularAbout.ENGLISH_NUMBER, "英文数字");

    private String regex;
    private String info;

    ValidateType() {
    }

    ValidateType(String regex, String info) {
        this.regex = regex;
        this.info = info;
    }

    @Override
    public ValidateMsg validateParam(String key, Object value, Validate validate) {
        if (value instanceof JSONNull) {
            value = null;
        }
        return validate(key, value, validate);
    }

    @Override
    public List<ValidateMsg> validateArray(String key, String[] value, Validate validate) {
        List<ValidateMsg> list = new ArrayList<>();
        for (String o : value) {
            ValidateMsg v = validate(key, o, validate);
            if (v != null) {
                list.add(v);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    private String createMessage(Validate validate) {
        if (StringUtil.isEmpty(validate.validateMsg()) && StringUtil.isEmpty(validate.validateMsgKey())) {
            return String.format("不符合%s格式", info);
        } else if (!StringUtil.isEmpty(validate.validateMsgKey())) {
            return PropertiesUtil.getMessage(validate.validateMsgKey(), (Object[]) validate.validateMsgParams());
        } else {
            return validate.validateMsg();
        }
    }

    private ValidateMsg validate(String key, Object value, Validate validate) {
        ValidateMsg v = new ValidateMsg(key, value);
        if (value != null) {
            boolean state;
            if (validate.validateType() != NO) {
                state = StringUtil.containMatchedString(regex, String.valueOf(value));
                if (!state) {
                    v.setState(false);
                    v.setMessage(createMessage(validate));
                }
            }
            if (!StringUtil.isEmpty(validate.validateRegex())) {
                state = StringUtil.containMatchedString(validate.validateRegex(), String.valueOf(value));
                if (!state) {
                    v.setState(false);
                    v.setMessage(createMessage(validate));
                }
            }
            switch (validate.validateType()) {
                case NUMBER:
                    int n = Integer.parseInt(String.valueOf(value));
                    if (!(validate.min() <= n && n <= validate.max())) {
                        v.setState(false);
                        v.setMessage("值超出阈值");
                    }
                case FLOAT:
                    float n1 = Float.parseFloat(String.valueOf(value));
                    if (!(validate.min() <= n1 && n1 <= validate.max())) {
                        v.setState(false);
                        v.setMessage("值超出阈值");
                    }
                default:
                    int size = String.valueOf(value).length();
                    if (!(validate.min_size() <= size && size <= validate.max_size())) {
                        v.setState(false);
                        v.setMessage("长度超出阈值");
                    }
            }
            return v;
        } else {
            if (validate.nullable()) {
                return null;
            }
            v.setState(false);
            v.setMessage("不允许为空值");
            return v;
        }
    }
}
