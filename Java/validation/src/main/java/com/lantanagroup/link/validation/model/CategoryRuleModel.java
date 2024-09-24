package com.lantanagroup.link.validation.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.fhir.r4.model.OperationOutcome;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CompositeCategoryRuleModel.class),
        @JsonSubTypes.Type(PatternMatchingCategoryRuleModel.class)
})
public interface CategoryRuleModel {
    boolean isMatch(OperationOutcome.OperationOutcomeIssueComponent issue);
}
