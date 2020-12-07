package cloud.agileframework.mvc.container;

import cloud.agileframework.common.util.file.ResponseFile;
import cloud.agileframework.common.util.file.poi.ExcelFile;
import cloud.agileframework.mvc.util.ViewUtil;
import cloud.agileframework.mvc.view.FileView;
import cloud.agileframework.mvc.view.FileViewResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.File;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020-12-03 14:26
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class FileHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ExcelFile.class.isAssignableFrom(returnType.getParameterType())
                || ResponseFile.class.isAssignableFrom(returnType.getParameterType())
                || File.class.isAssignableFrom(returnType.getParameterType());
    }


    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        List<Object> files = ViewUtil.extractFiles(returnValue);
        if (!files.isEmpty()) {
            mavContainer.setViewName(FileViewResolver.DEFAULT_VIEW_NAME);
            mavContainer.addAttribute(FileView.FILE_ATTRIBUTE_NAME, files);
        }
    }
}
