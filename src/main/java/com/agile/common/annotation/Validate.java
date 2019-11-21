package com.agile.common.annotation;

import com.agile.common.validate.ValidateType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 佟盟 on 2018/11/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validate {
    String value() default "";

    ValidateType validateType() default ValidateType.NO;

    String validateRegex() default "";

    String validateMsg() default "";

    String validateMsgKey() default "";

    Class[] validateGroups() default {};

    String[] validateMsgParams() default {};

    Class<?> beanClass() default Class.class;

    boolean nullable() default true;

    boolean isBlank() default true;

    double max() default Double.MAX_VALUE;

    double min() default Double.MIN_VALUE;

    int max_size() default Integer.MAX_VALUE;

    int min_size() default -1;
}
