package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.models.ResultModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.OperationOutcome;

@Getter
@Setter
@Entity
@Table(name = "result")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEntity {
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

    public ResultEntity() {
    }

    public ResultEntity(ResultModel model) {
        this.severity = model.getSeverity();
        this.code = model.getCode();
        this.message = model.getMessage();
        this.location = model.getLocation();
        this.expression = model.getExpression();
    }

    public ResultModel toModel() {
        ResultModel model = new ResultModel();
        model.setSeverity(this.severity);
        model.setCode(this.code);
        model.setMessage(this.message);
        model.setLocation(this.location);
        model.setExpression(this.expression);
        return model;
    }
}
