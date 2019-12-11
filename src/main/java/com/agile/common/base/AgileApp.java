package com.agile.common.base;

import org.springframework.boot.SpringApplication;

/**
 * 描述：
 * <p>创建时间：2019/1/9<br>
 * 该类在后续版本中删除，Agile项目启动可以直接以普通springboot工程方式启动
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Deprecated
public class AgileApp {

    public static void run(Class<?> primarySource, String[] args) {
        SpringApplication app = new SpringApplication(primarySource);
        app.run(args);
    }
}
