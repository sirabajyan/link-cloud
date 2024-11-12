package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {
    @Transient
    public static final Category UNCATEGORIZED;

    static {
        UNCATEGORIZED = new Category();
        UNCATEGORIZED.setId("uncategorized");
        UNCATEGORIZED.setTitle("Uncategorized");
        UNCATEGORIZED.setSeverity(CategorySeverity.WARNING);
        UNCATEGORIZED.setAcceptable(false);
        UNCATEGORIZED.setGuidance("These issues need to be categorized.");
    }

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorySeverity severity;

    @Column(nullable = false)
    private boolean acceptable;

    @Column(length = 1000, nullable = false)
    private String guidance;
}
