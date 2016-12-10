package com.nix.config;

import com.nix.api.soap.UserWebService;
import com.nix.api.soap.UserWebServiceImpl;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfig {

    @Bean(destroyMethod = "shutdown", name = "cxf")
    public SpringBus cxf() {
        SpringBus springBus = new SpringBus();

        springBus.getInInterceptors().add(loggingInInterceptor());
        springBus.getInInterceptors().add(loggingInInterceptor());

        return springBus;
    }

    @Bean
    public UserWebService userWebService() {
        return new UserWebServiceImpl();
    }

    @Bean
    public Endpoint usersEndpoint() {
        Endpoint endpoint = new EndpointImpl(cxf(), userWebService());
        endpoint.publish("/users");
        return endpoint;
    }

    @Bean()
    public LoggingInInterceptor loggingInInterceptor() {
        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        return loggingInInterceptor;
    }
}
