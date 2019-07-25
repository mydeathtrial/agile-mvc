package com.agile.mvc;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.mvc.service.MainService;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author 佟盟
 * 日期 2019/6/18 15:57
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Service
public class TestService extends MainService {


    @Mapping(value = "/test/{asd:.+}")
    public Object test() {

        LoggerFactory.getLogger(this.getClass()).debug("111111");
        LogFactory.getLog(this.getClass()).debug("22222");
        return RETURN.SUCCESS;
    }

    /**
     * 获取指定图片数据
     */
    @Mapping(path = "/test")
    public Object test2() {
        System.gc();

        return new File("D:\\workspace-idss\\agile\\src\\main\\resources\\asd.json");
    }
}
