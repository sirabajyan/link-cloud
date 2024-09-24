package com.lantanagroup.link.validation.controllers;

import com.lantanagroup.link.validation.entities.CategoryEntity;
import com.lantanagroup.link.validation.entities.CategoryRuleEntity;
import com.lantanagroup.link.validation.model.BulkSaveCategoryModel;
import com.lantanagroup.link.validation.model.CategoryRuleModel;
import com.lantanagroup.link.validation.repositories.CategoryRepository;
import com.lantanagroup.link.validation.repositories.CategoryRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
@SecurityRequirement(name = "bearer-key")
public class CategoryController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CategoryController.class);

    private final CategoryRepository categoryRepository;
    private final CategoryRuleRepository categoryRuleRepository;

    public CategoryController(CategoryRepository categoryRepository, CategoryRuleRepository categoryRuleRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryRuleRepository = categoryRuleRepository;
    }

    @Operation(summary = "Create or update a category", tags = {"Categories"})
    @PostMapping
    public void createOrUpdateCategory(@RequestBody CategoryEntity category) {
        if (StringUtils.isEmpty(category.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID is required");
        }

        if (StringUtils.isEmpty(category.getGuidance())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category guidance is required");
        }

        log.info("Creating/updating category with ID: {}", category.getId());

        this.categoryRepository.save(category);
    }

    @Operation(summary = "Get all categories", tags = {"Categories"})
    @GetMapping
    public List<CategoryEntity> getCategories() {
        return this.categoryRepository.findAll();
    }

    @Operation(summary = "Get the latest version of rules for a category by ID", tags = {"Categories"})
    @GetMapping("/{categoryId}/rules")
    public CategoryRuleModel getCategoryRules(@PathVariable String categoryId) {
        CategoryRuleEntity categoryRule = this.categoryRuleRepository.getLatestByCategoryId(categoryId);

        if (categoryRule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category rules not found");
        }

        return categoryRule.getModel();
    }

    @Operation(summary = "Create or update rules for a category by ID", tags = {"Categories"})
    @PostMapping("/{categoryId}/rules")
    public void createOrUpdateCategoryRules(@PathVariable String categoryId, @RequestBody CategoryRuleModel categoryRule) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID is required");
        }

        if (categoryRule == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category rules are required");
        }

        log.info("Creating/updating rules for category with ID: {}", categoryId);

        CategoryEntity category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        CategoryRuleEntity entity = new CategoryRuleEntity();
        entity.setCategory(category);
        entity.setModel(categoryRule);

        this.categoryRuleRepository.save(entity);
    }

    @Operation(summary = "Get the history of rules for a category by ID", tags = {"Categories"})
    @GetMapping("/{categoryId}/rules/history")
    public List<CategoryRuleModel> getCategoryRulesHistory(@PathVariable String categoryId) {
        return this.categoryRuleRepository.findByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(CategoryRuleEntity::getTimestamp, Comparator.reverseOrder()))
                .map(CategoryRuleEntity::getModel)
                .toList();
    }

    @Operation(summary = "Bulk save categories and their rules", tags = {"Categories"})
    @PostMapping("/bulk")
    @Transactional
    public void bulkSaveCategories(@RequestBody List<BulkSaveCategoryModel> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categories are required");
        } else if (categories.stream().anyMatch(category -> StringUtils.isEmpty(category.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required for all categories");
        }

        boolean hasDuplicateIds = categories.stream()
                .collect(Collectors.groupingBy(BulkSaveCategoryModel::getId, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);

        if (hasDuplicateIds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate category IDs are not allowed");
        }

        boolean hasMissingRules = categories.stream().anyMatch(category -> category.getRule() == null);

        if (hasMissingRules) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rules are required for all categories");
        }

        log.info("Bulk saving {} categories", categories.size());

        for (BulkSaveCategoryModel category : categories) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(category.getId());
            categoryEntity.setTitle(category.getTitle());
            categoryEntity.setSeverity(category.getSeverity());
            categoryEntity.setAcceptable(category.isAcceptable());
            categoryEntity.setGuidance(category.getGuidance());
            this.categoryRepository.save(categoryEntity);

            CategoryRuleModel rule = category.getRule();
            CategoryRuleEntity categoryRuleEntity = new CategoryRuleEntity();
            categoryRuleEntity.setCategory(categoryEntity);
            categoryRuleEntity.setModel(rule);
            this.categoryRuleRepository.save(categoryRuleEntity);
        }
    }

    @Operation(summary = "Delete a category by ID", tags = {"Categories"})
    @DeleteMapping("/{categoryId}")
    @Transactional
    public void deleteCategory(@PathVariable String categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        this.categoryRuleRepository.deleteByCategoryId(categoryId);
        this.categoryRepository.deleteById(categoryId);
    }
}
