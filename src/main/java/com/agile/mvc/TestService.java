package com.agile.mvc;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.mvc.service.MainService;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 佟盟
 * 日期 2019/6/18 15:57
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Service
public class TestService extends MainService {


    @Mapping(value = "/test")
    public Object test() {
        LoggerFactory.getLogger(this.getClass()).debug("111111");
        LogFactory.getLog(this.getClass()).debug("22222");
        return RETURN.SUCCESS;
    }

    public void test2() {
        LoggerFactory.getLogger(this.getClass()).debug("111111");
        LogFactory.getLog(this.getClass()).debug("22222");
    }
}
