package com.agile.common.view;

import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.util.FileUtil;
import com.agile.common.util.JSONUtil;
import com.agile.common.util.ViewUtil;
import net.sf.json.xml.XMLSerializer;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/8/1
 */
public class XmlView extends AbstractView {

    public static final String DEFAULT_CONTENT_TYPE = "application/xml";
    private final int byteSize = 1024;

    public XmlView() {
        this.setContentType(DEFAULT_CONTENT_TYPE);
        this.setExposePathVariables(false);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ViewUtil.Model target = ViewUtil.modelProcessing(model);
        List<Object> files = target.getFiles();
        if (files.size() > 0) {
            FileUtil.downloadFile(files, request, response);
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(byteSize);
            model.entrySet().removeIf(o -> o.getValue() instanceof BindingResult);

            XMLSerializer xmlSerializer = new XMLSerializer();
//        xmlSerializer.setTypeHintsEnabled(false);
//        xmlSerializer.setTypeHintsCompatibility(false);
            xmlSerializer.setObjectName("Entity");
            xmlSerializer.setArrayName("Array");
            xmlSerializer.setElementName("Element");
            String xml;
            try {
                xml = xmlSerializer.write(JSONUtil.toJSON(model));
            } catch (Exception e) {
                assert RETURN.XML_SERIALIZER_ERROR != null;
                xml = xmlSerializer.write(JSONUtil.toJSON(new Head(RETURN.XML_SERIALIZER_ERROR)));
            }
            byteArrayOutputStream.write(xml.getBytes(response.getCharacterEncoding()));
            this.setResponseContentType(request, response);
            response.setContentLength(byteArrayOutputStream.size());
            byteArrayOutputStream.writeTo(response.getOutputStream());
        }
    }
}
