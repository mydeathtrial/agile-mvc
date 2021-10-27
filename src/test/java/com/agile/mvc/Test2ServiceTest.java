package com.agile.mvc;

import cloud.agileframework.mvc.filter.RequestWrapperFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//AbstractTransactionalJUnit4SpringContextTests
public class Test2ServiceTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SharedHttpSessionConfigurer.sharedHttpSession())
                .addFilter(new RequestWrapperFilter())
                .alwaysDo(print())
                .alwaysExpect(status().isOk())
                .build();  //构造MockMvc
    }

    @Test
    public void test() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[0]);
        MvcResult r = mockMvc.perform(multipart("/test2/{id}?a=2", 1)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8"))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(r))
                .andExpect(status().isOk())
                .andExpect(jsonPath("head").exists())
                .andExpect(jsonPath("head.code").value("000000"))
                .andExpect(jsonPath("params").isString())
                .andExpect(jsonPath("urlV1").value(2))
                .andExpect(jsonPath("urlV2").value(1))
                .andExpect(jsonPath("file").value(1))
                .andReturn();

    }

    @Test
    public void test2() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[0]);
        MvcResult r = mockMvc.perform(multipart("/test2/{id}?a=2", 1)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8"))
                .andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(r))
                .andExpect(status().isOk())
                .andExpect(jsonPath("head").exists())
                .andExpect(jsonPath("head.code").value("100013"))
                .andExpect(jsonPath("result").isArray())
                .andExpect(jsonPath("result[0].item").value("a"))
                .andExpect(jsonPath("result[0].itemValue").value("2"))
                .andExpect(jsonPath("result[0].message").value("长度超出阈值;业务验证"))
                .andReturn();

    }

}
