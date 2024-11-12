package com.lantanagroup.link.validation.matchers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.fhir.r4.model.OperationOutcome;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CompositeMatcher.class),
        @JsonSubTypes.Type(PatternMatcher.class)
})
public interface Matcher {
    boolean isMatch(OperationOutcome.OperationOutcomeIssueComponent issue);
}
