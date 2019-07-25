package com.agile.mvc;

import com.agile.common.annotation.EnableAgile;
import com.agile.common.base.AgileApp;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration;

/**
 * 入口工程
 *
 * @author tudou
 */
//@EnableDiscoveryClient
//@EnableFeignClients
@EnableAgile
@SpringBootApplication(exclude = {
        WebFluxAutoConfiguration.class,
        WebServiceTemplateAutoConfiguration.class,
        WebServicesAutoConfiguration.class,
        XADataSourceAutoConfiguration.class,
        GroovyTemplateAutoConfiguration.class,
        GsonAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class})
public class App {
    public static void main(String[] args) {
        AgileApp.run(App.class, args);
    }
}
