package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import com.agile.common.base.AgileApp;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

/**
 * 入口工程
 */
@EnableAgile
@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
public class App {
    public static void main(String[] args) {
        AgileApp.run(App.class, args);
    }
}
