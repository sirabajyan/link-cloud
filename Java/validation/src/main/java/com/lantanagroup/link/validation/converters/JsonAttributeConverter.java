package com.lantanagroup.link.validation.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.UncheckedIOException;

public abstract class JsonAttributeConverter<T> implements AttributeConverter<T, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract Class<T> getAttributeClass();

    @Override
    public String convertToDatabaseColumn(T model) {
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public T convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, getAttributeClass());
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
