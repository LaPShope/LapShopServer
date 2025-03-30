package com.example.demo.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Enums.Role, String> {
    @Override
    public String convertToDatabaseColumn(Enums.Role role) {
        if (role == null) {
            return null;
        }

        return role.name();
    }

    @Override
    public Enums.Role convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }

        return Enums.Role.valueOf(s);
    }
}
