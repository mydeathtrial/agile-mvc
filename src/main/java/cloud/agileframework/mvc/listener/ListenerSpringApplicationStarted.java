package cloud.agileframework.mvc.listener;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.time.Duration;

/**
 * @author 佟盟
 * 日期 2020/1/8 16:29
 * 描述 工程完成启动事件
 * @version 1.0
 * @since 1.0
 */
public class ListenerSpringApplicationStarted implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ProjectContextHolder.setConsumeTime(Duration.ofMillis(ListenerSpringApplicationRun.getConsume()));
        ProjectContextHolder.setStatus(STATUS.RUNNING);
        ProjectContextHolder.print();
    }
}
