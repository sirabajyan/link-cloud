package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.converters.CategoryRuleModelConverter;
import com.lantanagroup.link.validation.model.CategoryRuleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "category_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_rule_category_id"))
    private CategoryEntity category;

    @Column(nullable = false)
    private OffsetDateTime timestamp = OffsetDateTime.now();

    @Convert(converter = CategoryRuleModelConverter.class)
    @Column(columnDefinition = "varchar(max)", nullable = false)
    private CategoryRuleModel model;
}
