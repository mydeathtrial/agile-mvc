package com.agile.common.listener;

import com.agile.common.util.DateUtil;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.web.context.WebServerInitializedEvent;

import java.time.Duration;
import java.util.Date;

/**
 * @author 佟盟
 * 日期 2019/7/29 15:20
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ProjectContextHolder {
    private static STATUS status;
    private static Date startedTime = new Date();
    private static int port;
    private static Duration consumeTime;

    protected static void event(WebServerInitializedEvent event) {
        ProjectContextHolder.port = event.getWebServer().getPort();
    }

    static void setConsumeTime(Duration consumeTime) {
        ProjectContextHolder.consumeTime = consumeTime;
    }

    static void print() {
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, "敏捷开发框架 Agile Framework"));
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, "启动状态: ", AnsiColor.BLUE, status.name()));
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, "启动时间: ", AnsiColor.BLUE, DateUtil.convertToString(startedTime, "yyyy年MM月dd日 HH:mm:ss")));
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, "启动端口: ", AnsiColor.BLUE, port));
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, "启动耗时: ", AnsiColor.BLUE, consumeTime.toString()));
    }

    static void setStatus(STATUS status) {
        ProjectContextHolder.status = status;
    }
}
