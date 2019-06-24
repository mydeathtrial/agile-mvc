package com.agile.common.listener;

import com.agile.common.util.DateUtil;
import com.agile.common.util.PrintUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 佟盟 on 2018/11/9
 */
@Component
public class ListenerContainerRefreshed implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        long current = contextRefreshedEvent.getTimestamp();
        String currentTime = DateUtil.convertToString(new Date(current), "yyyy年MM月dd日 HH:mm:ss");
        PrintUtil.writeln(" :: 已成功刷新容器 :: ", PrintUtil.YELLOW);
        PrintUtil.write(" :: 刷新时间 :: ", PrintUtil.CYAN);
        PrintUtil.writeln(currentTime, PrintUtil.YELLOW);
    }

}
