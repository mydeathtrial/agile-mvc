package com.agile.common.generator;

import com.agile.common.base.ApiInfo;
import com.agile.common.util.ApiUtil;
import com.agile.mvc.App;
import com.agile.mvc.controller.MainControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/25 18:47
 * @Description TODO
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class ShowdocGenerator extends MainControllerTest {

    @Test
    public void parsingApi() {
        Collection<ApiInfo> apis = ApiUtil.getApiInfoCache();
    }

}
