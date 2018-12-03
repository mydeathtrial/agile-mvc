package com.agile.common.annotation;

import java.lang.annotation.Annotation;

/**
 * 描述：
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface Parsing {
    Class<? extends Annotation> getAnnotation();
}
