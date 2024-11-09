package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.model.CategorySeverity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryEntity {
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

    public static CategoryEntity uncategorized() {
        CategoryEntity category = new CategoryEntity();
        category.setId("uncategorized");
        category.setTitle("Uncategorized");
        category.setSeverity(CategorySeverity.WARNING);
        category.setAcceptable(false);
        category.setGuidance("These issues need to be categorized.");
        return category;
    }
}
