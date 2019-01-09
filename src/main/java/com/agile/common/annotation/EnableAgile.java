package com.agile.common.annotation;

import com.agile.common.config.SpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 描述：
 * <p>创建时间：2019/1/8<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({SpringConfig.class})
@ComponentScan(basePackages = {"com.agile.**"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = ExcludeComponentScan.class)})
public @interface EnableAgile {
}
