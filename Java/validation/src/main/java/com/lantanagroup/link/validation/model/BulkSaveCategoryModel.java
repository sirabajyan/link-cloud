package com.lantanagroup.link.validation.model;

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
}
