package com.lantanagroup.link.validation.models;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.OperationOutcome;

@Getter
@Setter
public class ResultModel {
    private OperationOutcome.IssueSeverity severity;
    private OperationOutcome.IssueType code;
    private String message;
    private String location;
    private String expression;
}
