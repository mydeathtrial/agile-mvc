package com.agile.common.listener.event;

import org.springframework.context.ApplicationListener;

/**
 * @author 佟盟
 * 日期 2019/7/29 14:32
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ListenerTest implements ApplicationListener<EventTest> {
    @Override
    public void onApplicationEvent(EventTest event) {
    }
}
