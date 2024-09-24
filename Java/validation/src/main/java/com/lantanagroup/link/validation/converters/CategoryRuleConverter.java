package com.lantanagroup.link.validation.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lantanagroup.link.validation.model.CategoryRuleModel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.UncheckedIOException;

@Converter
public class CategoryRuleConverter implements AttributeConverter<CategoryRuleModel, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CategoryRuleModel model) {
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CategoryRuleModel convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, CategoryRuleModel.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
