package cloud.agileframework.mvc.view;

import cloud.agileframework.common.util.file.FileUtil;
import cloud.agileframework.common.util.file.ResponseFile;
import cloud.agileframework.common.util.stream.ThrowingConsumer;
import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020-12-03 11:45
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class FileView extends AbstractView {
    private final String fileName;
    private final ThrowingConsumer<HttpServletResponse> write;

    public FileView(File file) {
        this.fileName = file.getName();
        write = response -> IOUtils.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
    }

    public FileView(ResponseFile file) {
        this.fileName = file.getFileName();
        write = file::write;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileUtil.downloadFile(fileName, write, request, response);
    }
}
