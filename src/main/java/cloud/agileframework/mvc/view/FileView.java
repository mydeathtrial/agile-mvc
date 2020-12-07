package cloud.agileframework.mvc.view;

import cloud.agileframework.spring.util.MultipartFileUtil;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020-12-03 11:45
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class FileView extends AbstractView {
    public static final String FILE_ATTRIBUTE_NAME = "$AGILE_FILE_ATTRIBUTE_NAME";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object files = model.get(FILE_ATTRIBUTE_NAME);
        if (files != null) {
            MultipartFileUtil.downloadFile(files, request, response);
        }
    }
}
