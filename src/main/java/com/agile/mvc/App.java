package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 入口工程
 *
 * @author tudou
 */
//@EnableDiscoveryClient
//@EnableFeignClients
@EnableAgile
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
}
