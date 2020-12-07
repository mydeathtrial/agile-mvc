package cloud.agileframework.mvc.view;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

import java.util.Locale;

/**
 * @author 佟盟
 * 日期 2020-12-03 11:49
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class FileViewResolver extends AbstractCachingViewResolver {
    public static final String DEFAULT_VIEW_NAME = "file:";

    @Override
    protected View loadView(String viewName, Locale locale) {
        if (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName)) {
            return new FileView();
        }
        return null;
    }
}
