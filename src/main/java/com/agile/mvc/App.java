package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);

        log.info("info");
        log.debug("debug");
        log.error("error",new RuntimeException("nishishu"));
    }
}
