package com.nix.api.rest.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nix.api.rest.deserializer.DateDeserializer;
import com.nix.api.rest.deserializer.RoleDeserializer;
import com.nix.api.rest.serializer.RoleSerializer;
import com.nix.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    @Autowired
    @Qualifier("roleSerializer")
    private RoleSerializer roleSerializer;

    @Autowired
    @Qualifier("roleDeserializer")
    private RoleDeserializer roleDeserializer;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        SimpleModule module = new SimpleModule();
        module.addSerializer(Role.class, roleSerializer);
        module.addDeserializer(Role.class, roleDeserializer);
        module.addDeserializer(Date.class, new DateDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
