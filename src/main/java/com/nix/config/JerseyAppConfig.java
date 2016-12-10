package com.nix.config;

import com.nix.api.rest.provider.ObjectMapperProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/rest/**")
public class JerseyAppConfig extends ResourceConfig {

    public JerseyAppConfig() {
        packages("com.nix.api.rest");
        register(ObjectMapperProvider.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }


}