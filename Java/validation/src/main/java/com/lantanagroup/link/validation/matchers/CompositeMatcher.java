package com.lantanagroup.link.validation.matchers;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.util.List;

@Getter
@Setter
public class CompositeMatcher extends InvertibleMatcher {
    private List<Matcher> children;
    private boolean requiresAllChildren;

    @Override
    protected boolean doIsMatch(OperationOutcome.OperationOutcomeIssueComponent issue) {
        if (CollectionUtils.isEmpty(children)) {
            throw new IllegalStateException("Composite rule must specify children");
        }
        for (Matcher child : children) {
            boolean isMatch = child.isMatch(issue);
            if (isMatch && !requiresAllChildren) {
                return true;
            }
            if (!isMatch && requiresAllChildren) {
                return false;
            }
        }
        return requiresAllChildren;
    }
}
