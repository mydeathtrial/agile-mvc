package cloud.agileframework.mvc.container;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

/**
 * @author 佟盟 on 2017/9/27
 */
public class ResetService implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.addInitializers(new WebInitializer());
    }
}
