package com.bybud.common.config;

import com.bybud.common.model.RoleName;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RoleNameDeserializer extends JsonDeserializer<Set<RoleName>> {
    @Override
    public Set<RoleName> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Set<RoleName> roles = new HashSet<>();
        String[] roleStrings = p.readValueAs(String[].class);
        for (String role : roleStrings) {
            roles.add(RoleName.valueOf(role));
        }
        return roles;
    }
}

