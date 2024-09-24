package com.lantanagroup.link.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.util.regex.Pattern;

@Getter
@Setter
public class PatternMatchingCategoryRuleModel extends InvertibleCategoryRuleModel {
    private IssueField field;
    private String regex;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private Pattern pattern;

    public void setRegex(String regex) {
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    @Override
    protected boolean doIsMatch(OperationOutcome.OperationOutcomeIssueComponent issue) {
        if (field == null || pattern == null) {
            throw new IllegalStateException("Pattern-matching rule must specify field and pattern");
        }
        for (String value : field.getValues(issue)) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
