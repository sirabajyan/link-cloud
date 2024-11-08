package com.lantanagroup.link.validation.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.util.List;

@Getter
@Setter
public class CompositeCategoryRuleModel extends InvertibleCategoryRuleModel {
    private List<CategoryRuleModel> children;
    private boolean requiresAllChildren;

    @Override
    protected boolean doIsMatch(OperationOutcome.OperationOutcomeIssueComponent issue) {
        if (CollectionUtils.isEmpty(children)) {
            throw new IllegalStateException("Composite rule must specify children");
        }
        boolean shortCircuitResult = !requiresAllChildren;
        for (CategoryRuleModel child : children) {
            if (child.isMatch(issue) == shortCircuitResult) {
                return shortCircuitResult;
            }
        }
        return !shortCircuitResult;
    }
}
