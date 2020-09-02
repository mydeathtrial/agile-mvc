package cloud.agileframework.mvc.annotation;


/**
 * 描述：bean加载之前触发的注解解析器
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface ParsingBeanBefore extends Parsing {
    /**
     * 解析器方法
     *
     * @param beanName beanName
     * @param bean     bean
     */
    void parsing(String beanName, Object bean);
}
