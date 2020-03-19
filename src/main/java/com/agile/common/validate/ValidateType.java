package com.agile.common.validate;

import com.agile.common.annotation.Validate;
import com.agile.common.base.Constant;
import com.agile.common.util.NumberUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import org.apache.commons.compress.utils.Lists;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/16
 */
public enum ValidateType implements ValidateInterface {
    /**
     * 正则校验类型
     */
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
    INT(Constant.RegularAbout.INT, "证书"),
    DOUBLE(Constant.RegularAbout.DOUBLE, "双精度"),
    ENGLISH_NUMBER(Constant.RegularAbout.ENGLISH_NUMBER, "英文数字"),
    MAC(Constant.RegularAbout.MAC, "MAC地址");

    private String regex;
    private String info;

    ValidateType() {
    }

    ValidateType(String regex, String info) {
        this.regex = regex;
        this.info = info;
    }

    @Override
    @NotNull
    public List<ValidateMsg> validateParam(String key, Object value, Validate validate) {
        return validate(key, value, validate);
    }

    @Override
    @NotNull
    public List<ValidateMsg> validateArray(String key, List<Object> value, Validate validate) {
        List<ValidateMsg> list = Lists.newArrayList();
        for (int i = 0; i < value.size(); i++) {
            Object node = value.get(i);
            List<ValidateMsg> vs = validate(String.format("%s.%s", key, i), node, validate);
            if (ObjectUtil.isEmpty(vs)) {
                continue;
            }
            list.addAll(vs);
        }

        int size = value.size();
        if (!(validate.min_size() <= size && size <= validate.max_size())) {
            ValidateMsg v = new ValidateMsg(createMessage(validate, "长度超出阈值"), false, key, value);
            list.add(v);
        }
        return list;
    }

    private String createMessage(Validate validate, String defaultMessage) {
        String result;
        if (StringUtil.isBlank(validate.validateMsg()) && StringUtil.isBlank(validate.validateMsgKey()) && defaultMessage == null) {
            result = String.format("不符合%s格式", info == null ? "自定义" : info);
        } else if (!StringUtil.isBlank(validate.validateMsg())) {
            result = validate.validateMsg();
        } else if (!StringUtil.isBlank(validate.validateMsgKey())) {
            result = PropertiesUtil.getMessage(validate.validateMsgKey(), (Object[]) validate.validateMsgParams());
        } else {
            result = defaultMessage;
        }
        return result;
    }

    private String createMessage(Validate validate) {
        return createMessage(validate, null);
    }

    private List<ValidateMsg> validate(String key, Object value, Validate validate) {
        List<ValidateMsg> list = new ArrayList<>();

        Class<?> beanClass = validate.beanClass();
        if (beanClass != Class.class) {
            Object bean = ObjectUtil.cast(beanClass, value);
            if (bean == null) {
                list.add(new ValidateMsg(value == null ? "参数不允许为空" : "非法参数", false, key, value));
            } else {
                ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
                Validator validator = validatorFactory.getValidator();
                Set<ConstraintViolation<Object>> set = validator.validate(bean, validate.validateGroups());
                for (ConstraintViolation<Object> m : set) {
                    ValidateMsg r = new ValidateMsg(m.getMessage(), false, StringUtil.isBlank(key) ? m.getPropertyPath().toString() : String.format("%s.%s", key, m.getPropertyPath()), m.getInvalidValue());
                    list.add(r);
                }
            }

            return list;
        }

        ValidateMsg v = new ValidateMsg(key, value);
        list.add(v);
        if (value != null) {
            boolean state;
            if (!validate.isBlank()) {
                state = !StringUtil.isBlank(value.toString());
                if (!state) {
                    v.setState(false);
                    v.setMessage(createMessage(validate, "不允许为空值"));
                }
            }
            if (!StringUtil.isBlank(value.toString()) && validate.validateType() != NO) {
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
            if (validate.validateType() != NO) {
                state = StringUtil.containMatchedString(validate.validateType().regex, String.valueOf(value));
                if (!state) {
                    v.setState(false);
                    v.setMessage(createMessage(validate));
                }
            }
            if (validate.validateType() == NUMBER
                    || validate.validateType() == FLOAT
                    || validate.validateType() == INT
                    || validate.validateType() == DOUBLE) {
                Number n = "".equals(value) ? 0 : NumberUtil.createNumber(String.valueOf(value));
                if (!(validate.min() <= n.doubleValue() && n.doubleValue() <= validate.max())) {
                    v.setState(false);
                    v.setMessage(createMessage(validate, "值超出阈值"));
                }
            }
            int size = String.valueOf(value).length();
            if (!(validate.min_size() <= size && size <= validate.max_size())) {
                v.setState(false);
                v.setMessage(createMessage(validate, "长度超出阈值"));
            }
        } else {
            if (!validate.nullable()) {
                v.setState(false);
                v.setMessage(createMessage(validate, "不允许为空值"));
            }
        }
        return list;
    }
}
