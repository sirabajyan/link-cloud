package com.lantanagroup.link.validation.model;

import com.lantanagroup.link.validation.entities.CategoryEntity;
import com.lantanagroup.link.validation.entities.CategoryRuleEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Model of a single category and its rule sets to be used by the API's bulk save endpoint.
 */
@Getter
@Setter
public class BulkSaveCategoryModel {
    private String id;
    private String title;
    private CategorySeverity severity;
    private boolean acceptable;
    private String guidance;
    private CategoryRuleModel rule;

    public CategoryEntity toEntity() {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setSeverity(severity);
        entity.setAcceptable(acceptable);
        entity.setGuidance(guidance);
        return entity;
    }

    public CategoryRuleEntity toRuleEntity(CategoryEntity entity) {
        CategoryRuleEntity ruleEntity = new CategoryRuleEntity();
        ruleEntity.setCategory(entity);
        ruleEntity.setModel(rule);
        return ruleEntity;
    }
}
