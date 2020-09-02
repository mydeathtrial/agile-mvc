package cloud.agileframework.mvc.listener;

import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.spring.util.spring.PrintUtil;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 佟盟 on 2018/11/9
 */
public class ListenerContainerRefreshed implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        long current = contextRefreshedEvent.getTimestamp();
        String currentTime = DateUtil.toFormatByDate(new Date(current), "yyyy年MM月dd日 HH:mm:ss");
        PrintUtil.println(AnsiOutput.toString(AnsiColor.GREEN, " :: 已成功刷新容器 :: "));
        PrintUtil.println(AnsiOutput.toString(AnsiColor.GREEN, " :: 刷新时间 :: ", AnsiColor.BLUE, currentTime));
    }

}
