package cloud.agileframework.mvc.view;

import cloud.agileframework.common.util.clazz.ClassUtil;
import cloud.agileframework.mvc.base.Constant;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author 佟盟 on 2017/8/1
 */
public class PlainView extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private final Integer byteSize = 1024;

    public PlainView() {
        this.setContentType(DEFAULT_CONTENT_TYPE);
        this.setExposePathVariables(false);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteSize);
        if (model.containsKey(Constant.ResponseAbout.RESULT)) {
            Object r = model.get(Constant.ResponseAbout.RESULT);
            if (r instanceof Map) {
                for (Object value : ((Map<String, Object>) r).values()) {
                    if (ClassUtil.isPrimitiveOrWrapper(value.getClass()) || value instanceof String) {
                        baos.write(value.toString().getBytes(response.getCharacterEncoding()));
                    }
                }
            } else if (r instanceof String) {
                baos.write(r.toString().getBytes(response.getCharacterEncoding()));
            }
        }
        response.setContentType("text/html");
        response.setContentLength(baos.size());
        baos.writeTo(response.getOutputStream());
    }
}
