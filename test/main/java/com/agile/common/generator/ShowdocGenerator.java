package com.agile.common.generator;

import com.agile.mvc.App;
import com.agile.mvc.controller.MainControllerTest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/25 18:47
 * @Description TODO
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShowdocGenerator extends MainControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();  //构造MockMvc
    }

    @Test
    public void listAll() throws Exception {
        ArrayList<Contact> contacts = Lists.newArrayList(
                new Contact("2018-09-02 10:31:41", "74", "被叫", "02259925401", "上海"),
                new Contact("2018-09-02 11:27:24", "114", "被叫", "02759279698", "上海"),
                new Contact("2018-09-02 11:29:36", "205", "被叫", "02259925463", "上海"),
                new Contact("2018-09-02 11:36:05", "25", "被叫", "02259991437", "上海")
        );

        InputInfo inputInfo = new InputInfo();
        inputInfo.setBackTrackingPoint("");
        inputInfo.setIdCard("430422199009098899");
        inputInfo.setMobile("13077316470");
        inputInfo.setToken("3497d797715f4710966678e945d6d348");
        inputInfo.setTrueName("sa");
        inputInfo.setContacts(contacts);

        String jsonData = JSON.toJSONString(inputInfo);

//        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/test")
//                .content(jsonData).contentType(MediaType.APPLICATION_JSON)) // json 参数和类型
//                .andExpect(status().is(200)).andDo(print())
//                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
//        System.out.println("-----返回的json = " + responseString);

        MvcResult result = mockMvc.perform(post("/test").content(jsonData).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()) // 期待返回状态吗码200
                // JsonPath expression  https://github.com/jayway/JsonPath
                //.andExpect(jsonPath("$[1].name").exists()) // 这里是期待返回值是数组，并且第二个值的 name 存在，所以这里测试是失败的
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Data
    @AllArgsConstructor
    public class Contact {
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;
    }

    @Data
    private class InputInfo {
        private String backTrackingPoint;
        private String idCard;
        private String mobile;
        private String token;
        private String trueName;
        private List<Contact> contacts;

    }
}
