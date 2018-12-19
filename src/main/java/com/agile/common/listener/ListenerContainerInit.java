package com.agile.common.listener;

import com.agile.common.util.PrintUtil;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by 佟盟 on 2018/11/9
 */
@Component
public class ListenerContainerInit implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        PrintUtil.write(" :: 启动端口 :: ", PrintUtil.CYAN);
        PrintUtil.writeln(event.getWebServer().getPort() + "\n", PrintUtil.YELLOW);
    }
}
