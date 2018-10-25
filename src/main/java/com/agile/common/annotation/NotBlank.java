package com.agile.common.annotation;

import java.lang.annotation.*;

/**
 * Created by 佟盟 on 2018/10/25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlank {
    String param();
    String message() default "{NotBlank}";
}
