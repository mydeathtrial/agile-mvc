package cloud.agileframework.mvc.annotation;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：描述RequestMethod注解
 * <p>创建时间：2018/12/25<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {
    RequestMethod[] value();
}
