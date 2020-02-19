package com.agile.common.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author 佟盟
 * 日期 2019/7/29 14:32
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class EventTest extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public EventTest(Object source) {
        super(source);
    }
}
