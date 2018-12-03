package com.agile.common.annotation;


/**
 * 描述：
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface ParsingBeanBefore extends Parsing{
    void parsing(String beanName,Object bean);
}
