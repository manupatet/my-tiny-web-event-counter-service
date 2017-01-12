package com.expedia.www.my.tiny.web.event.counter.config;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration is the standalone-equivalent of what goed into web.xml Jerseyservlet config
 * @author mpatet
 *
 */
@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RequestContextFilter.class);
        packages(true, "com.expedia.www.my.tiny.web.event.counter.http");
        register(LoggingFilter.class);
    }
}