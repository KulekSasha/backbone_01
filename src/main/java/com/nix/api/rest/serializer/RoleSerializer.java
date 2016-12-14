package com.nix.api.rest.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nix.model.Role;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleSerializer extends JsonSerializer<Role> {

    @Override
    public void serialize(Role value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getName());
    }
}
