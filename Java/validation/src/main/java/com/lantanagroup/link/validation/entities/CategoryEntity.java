package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.model.CategorySeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class CategoryEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private CategorySeverity severity;

    @Column(nullable = false)
    private boolean acceptable;

    @Column(nullable = false)
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
