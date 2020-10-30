package com.agile;

import cloud.agileframework.mvc.base.RETURN;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 佟盟
 * 日期 2020/8/00031 19:32
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
    
    /**
     * 描述：TODO: 
     * 
     * @author 佟盟
     * @date 2020-10-30 9:56
    */
    @RequestMapping("/")
    public RETURN method() {
        
        return RETURN.SUCCESS;
    }
}
