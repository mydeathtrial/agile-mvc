package com.agile.mvc.service;

import com.agile.mvc.controller.MainControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by 佟盟
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DictionaryDataServiceTest extends MainControllerTest {
    @Test
    public void task2() throws Exception {
        this.setUrl("/api/DictionaryDataService/task2");
        this.setMethod("post");

        this.setParameter("setValue", "");
        this.setParameter("setKey", "");
        this.setParameter("setDictionaryDataId", "");
        this.setParameter("setDictionaryMainId", "");
        this.setParameter("setIsFixed", "");

        this.processor();
    }

    @Test
    public void task1() throws Exception {
        this.setUrl("/api/DictionaryDataService/task1");
        this.setMethod("post");

        this.setParameter("setValue", "");
        this.setParameter("setKey", "");
        this.setParameter("setDictionaryDataId", "");
        this.setParameter("setDictionaryMainId", "");
        this.setParameter("setIsFixed", "");

        this.processor();
    }

    @Test
    public void a1() throws Exception {
        this.setUrl("/api/DictionaryDataService/a1");
        this.setMethod("post");

        this.setParameter("setValue", "");
        this.setParameter("setKey", "");
        this.setParameter("setDictionaryDataId", "");
        this.setParameter("setDictionaryMainId", "");
        this.setParameter("setIsFixed", "");

        this.processor();
    }

    @Test
    public void a2() throws Exception {
        this.setUrl("/api/DictionaryDataService/a2");
        this.setMethod("post");

        this.setParameter("setValue", "");
        this.setParameter("setKey", "");
        this.setParameter("setDictionaryDataId", "");
        this.setParameter("setDictionaryMainId", "");
        this.setParameter("setIsFixed", "");

        this.processor();
    }


}