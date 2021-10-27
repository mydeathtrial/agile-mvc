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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
//AbstractTransactionalJUnit4SpringContextTests
public class TestControllerTest {
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

        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("head").exists())
                .andExpect(jsonPath("head.code").value("000000"))
                .andExpect(jsonPath("name").value("BeJson"))
                .andReturn();
    }

    @Test
    public void test3() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("file", "a.txt", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[0]);
        MockMultipartFile file2 = new MockMultipartFile("file", "b.txt", MediaType.APPLICATION_OCTET_STREAM_VALUE, new byte[0]);

        mockMvc.perform(multipart("/test3?a=12")
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("head").exists())
                .andExpect(jsonPath("head.code").value("100003"))
                .andExpect(jsonPath("file").doesNotExist())
                .andExpect(jsonPath("a").doesNotExist())
                .andReturn();
    }

    @Test
    public void test4() throws Exception {
        mockMvc.perform(post("/test4?a=12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("head").exists())
                .andExpect(jsonPath("head.code").value("100013"))
                .andReturn();
    }
}
