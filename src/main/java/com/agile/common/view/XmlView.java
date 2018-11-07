package com.agile.common.view;

import com.agile.common.base.poi.ExcelFile;
import com.agile.common.util.FileUtil;
import com.agile.common.util.JSONUtil;
import net.sf.json.xml.XMLSerializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by 佟盟 on 2017/8/1
 */
@Component
public class XmlView extends AbstractView {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";

    public XmlView() {
        this.setContentType(DEFAULT_CONTENT_TYPE);
        this.setExposePathVariables(false);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Object value:model.values()) {
            boolean isFile = FileUtil.downloadFile(value, request, response);
            if(isFile)return;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
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
        }catch (Exception e){
            xml = xmlSerializer.write(JSONUtil.toJSON("{\"error\":\"内容包含特殊字符，无法完成xml序列化，请尝试json序列化\"}"));
        }
        byteArrayOutputStream.write(xml.getBytes(response.getCharacterEncoding()));
        this.setResponseContentType(request, response);
        response.setContentLength(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(response.getOutputStream());
    }
}
