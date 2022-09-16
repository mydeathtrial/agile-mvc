package cloud.agileframework.mvc.provider;

import cloud.agileframework.dictionary.util.ConvertDicAnnotation;

/**
 * @author 佟盟
 * 日期 2020/8/4 9:42
 * 描述 为JPA组件提供字典翻译扩展
 * @version 1.0
 * @since 1.0
 */
public class DictionaryProvider implements DataExtendManager {
    @Override
    public void cover(Object o) {
        ConvertDicAnnotation.cover(o);
    }
}
