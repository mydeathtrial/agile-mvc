package cloud.agileframework.mvc.annotation;

import cloud.agileframework.common.constant.Constant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 佟盟
 * 日期 2020-10-15 17:30
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AgileInParam {
    String value() default Constant.RequestAbout.BODY;
}
