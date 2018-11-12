package com.agile.common.container;

import com.agile.common.util.DateUtil;
import com.agile.common.util.PrintUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by 佟盟 on 2018/11/9
 */
@Component
public class ContainerListener implements ApplicationListener<ContextRefreshedEvent>{

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        long current = contextRefreshedEvent.getTimestamp();
        String currentTime = DateUtil.convertToString(new Date(current), "yyyy年MM月dd日 hh:mm:ss");
        PrintUtil.writeln("\n :: 敏捷开发框架 Agile Framework :: ",PrintUtil.CYAN);
        PrintUtil.write(" :: 启动状态 :: ",PrintUtil.CYAN);
        PrintUtil.writeln("已成功启动",PrintUtil.YELLOW);
        PrintUtil.write(" :: 启动时间 :: ",PrintUtil.CYAN);
        PrintUtil.writeln(currentTime ,PrintUtil.YELLOW);
    }
}
