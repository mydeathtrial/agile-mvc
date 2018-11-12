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
public class ContainerListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        long current = contextRefreshedEvent.getTimestamp();
        String currentTime = DateUtil.convertToString(new Date(current), "yyyy-MM-dd hh:mm:ss");
        PrintUtil.write(String.format("\n :: 敏捷开发框架 Agile Framework :: 已成功启动 启动时间时间:%s\n",currentTime),PrintUtil.CYAN);
    }
}
