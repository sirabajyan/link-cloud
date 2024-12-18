package com.lantanagroup.link.validation.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ResultSummary {
    private String value;
    private long count;

    public ResultSummary() {
    }

    public ResultSummary(String value, long count) {
        this.value = value;
        this.count = count;
    }

    public ResultSummary(Map.Entry<String, Long> entry) {
        this(entry.getKey(), entry.getValue());
    }
}
