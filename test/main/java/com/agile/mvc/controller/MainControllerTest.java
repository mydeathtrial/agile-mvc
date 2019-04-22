package com.agile.mvc.controller;

import com.agile.common.base.Constant;
import com.agile.common.container.MappingHandlerMapping;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.JSONUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.mvc.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.nio.charset.StandardCharsets;


/**
 * @author 佟盟 on 2017/5/5
 */
@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = {App.class})
public class MainControllerTest implements ApplicationContextAware {
    private static RequestMappingHandlerMapping handlerMapping;
    private static RequestMappingHandlerAdapter handlerAdapter;
    private static MockHttpServletRequest request;

    static {
        PropertiesUtil.merge("test.yml");
    }

    //日志工具
    private Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 启动测试之前，需要获取springMVC的请求映射与请求适配器，以便模拟服务器请求
     */
    @Before
    public void setUp() {
        handlerMapping = FactoryUtil.getBean(MappingHandlerMapping.class);
        if (handlerMapping == null) {
            handlerMapping = (RequestMappingHandlerMapping) FactoryUtil.getBean("requestMappingHandlerMapping");
        }
        handlerAdapter = FactoryUtil.getBean(RequestMappingHandlerAdapter.class);
        request = new MockHttpServletRequest();
    }

    /**
     * 提供给每个测试用例，用于设置请求服务的地址路径
     *
     * @param url 地址
     */
    protected void setUrl(String url) {
        request.setServletPath(url);
    }

    /**
     * 提供给每个测试用例，用于设置请求参数
     *
     * @param key   索引字符串
     * @param value 值
     */
    protected void setParameter(String key, String value) {
        request.setParameter(key, value);
    }

    /**
     * 设置body参数
     *
     * @param value 参数
     */
    protected void setBody(String value) {
        request.setContent(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 提供给每个测试用力，用于设置请求POST/GET方法
     *
     * @param method emn：post,get
     */
    protected void setMethod(String method) {
        request.setMethod(method);
    }

    /**
     * 测试用例核心处理器
     * 每个测试用例将继承于主测试控制器，并最终通过该核心处理器将测试请求发送至指定服务，并打印服务响应信息
     *
     * @throws Exception 异常
     */
    @Transactional
    protected void processor() throws Exception {
        request.setAttribute(HandlerMapping.INTROSPECT_TYPE_LEVEL_MAPPING, true);
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        ModelAndView model = null;
        try {
            if (chain != null) {
                model = handlerAdapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
            }
            if (model != null) {
                logger.info(JSONUtil.toJSONString(model.getModel().get(Constant.ResponseAbout.HEAD)));
                logger.trace(JSONUtil.toJSONString(model.getModel().get(Constant.ResponseAbout.RESULT)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        FactoryUtil.setApplicationContext(applicationContext);
    }
}