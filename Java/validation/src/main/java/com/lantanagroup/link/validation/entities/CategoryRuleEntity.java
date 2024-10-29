package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.converters.CategoryRuleConverter;
import com.lantanagroup.link.validation.model.CategoryRuleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "category_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class CategoryRuleEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false)
    private Date timestamp = new Date();

    @Convert(converter = CategoryRuleConverter.class)
    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private CategoryRuleModel model;
}
