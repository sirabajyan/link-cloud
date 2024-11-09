package com.lantanagroup.link.validation.converters;

import com.lantanagroup.link.validation.model.CategoryRuleModel;
import jakarta.persistence.Converter;

@Converter
public class CategoryRuleModelConverter extends JsonAttributeConverter<CategoryRuleModel> {
    @Override
    protected Class<CategoryRuleModel> getAttributeClass() {
        return CategoryRuleModel.class;
    }
}
