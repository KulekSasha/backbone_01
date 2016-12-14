package com.nix.api.rest.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nix.model.Role;
import com.nix.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleDeserializer extends JsonDeserializer<Role> {

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;

    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return roleService.findByName(p.getValueAsString("role"));
    }
}
