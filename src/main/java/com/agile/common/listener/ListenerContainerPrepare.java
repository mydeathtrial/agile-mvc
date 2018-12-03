package com.agile.common.listener;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 描述：
 * <p>创建时间：2018/11/30<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Component
public class ListenerContainerPrepare implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        System.out.println();
    }
}