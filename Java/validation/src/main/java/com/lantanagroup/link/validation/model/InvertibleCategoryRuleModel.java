package com.lantanagroup.link.validation.model;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.OperationOutcome;

@Getter
@Setter
public abstract class InvertibleCategoryRuleModel implements CategoryRuleModel {
    private boolean inverted;

    @Override
    public boolean isMatch(OperationOutcome.OperationOutcomeIssueComponent issue) {
        return doIsMatch(issue) != inverted;
    }

    protected abstract boolean doIsMatch(OperationOutcome.OperationOutcomeIssueComponent issue);
}
