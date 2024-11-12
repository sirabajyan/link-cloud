package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.OperationOutcome;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String reportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationOutcome.IssueSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationOutcome.IssueType code;

    @Column(columnDefinition = "varchar(max)", nullable = false)
    private String message;

    private String location;

    @Column(length = 1000)
    private String expression;
}
