package com.lantanagroup.link.validation.converters;

import com.lantanagroup.link.validation.matchers.Matcher;
import jakarta.persistence.Converter;

@Converter
public class MatcherConverter extends JsonAttributeConverter<Matcher> {
    @Override
    protected Class<Matcher> getAttributeClass() {
        return Matcher.class;
    }
}
