package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import com.agile.common.util.FactoryUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 入口工程
 *
 * @author tudou
 */
@EnableAgile
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
        System.out.println(FactoryUtil.getApplicationContext().getApplicationName());
    }
}
