package com.expedia.www.my.tiny.web.event.counter;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.expedia.www.my.tiny.web.event.counter.config.JerseyConfig;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.expedia.www.my.tiny.web.event.counter.http" })
public class Main {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(Main.class).showBanner(false).run(args);
    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        //We're making it a ROOT application, so no uriMappings in the ServletRegistrationBean constructor
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer());
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        return registration;
    }
}
