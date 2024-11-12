package com.lantanagroup.link.validation.matchers;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.PrimitiveType;

import java.util.List;

public enum IssueField {
    SEVERITY,
    CODE,
    DETAILS_TEXT,
    DIAGNOSTICS,
    EXPRESSION;

    public List<String> getValues(OperationOutcome.OperationOutcomeIssueComponent issue) {
        return switch (this) {
            case SEVERITY -> List.of(issue.getSeverity().toCode());
            case CODE -> List.of(issue.getCode().toCode());
            case DETAILS_TEXT -> List.of(issue.getDetails().getText());
            case DIAGNOSTICS -> List.of(issue.getDiagnostics());
            case EXPRESSION -> issue.getExpression().stream()
                    .map(PrimitiveType::getValue)
                    .toList();
        };
    }
}
