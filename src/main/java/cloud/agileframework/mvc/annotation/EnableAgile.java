package cloud.agileframework.mvc.annotation;

import cloud.agileframework.mvc.config.AgileAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
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
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Inherited
@Import({AgileAutoConfiguration.class})
public @interface EnableAgile {
}
