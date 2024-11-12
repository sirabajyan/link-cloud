package com.lantanagroup.link.validation.controllers;

import com.lantanagroup.link.validation.entities.Category;
import com.lantanagroup.link.validation.entities.CategoryRule;
import com.lantanagroup.link.validation.entities.CategorySnapshot;
import com.lantanagroup.link.validation.matchers.Matcher;
import com.lantanagroup.link.validation.repositories.CategoryRepository;
import com.lantanagroup.link.validation.repositories.CategoryRuleRepository;
import com.lantanagroup.link.validation.services.CategorizationService;
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
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CategoryController.class);

    private final CategoryRepository categoryRepository;
    private final CategoryRuleRepository categoryRuleRepository;
    private final CategorizationService categorizationService;

    public CategoryController(
            CategoryRepository categoryRepository,
            CategoryRuleRepository categoryRuleRepository,
            CategorizationService categorizationService) {
        this.categoryRepository = categoryRepository;
        this.categoryRuleRepository = categoryRuleRepository;
        this.categorizationService = categorizationService;
    }

    @Operation(summary = "Initialize categories", tags = {"Categories"})
    @PostMapping("/$init")
    public void initCategories() {
        this.categorizationService.initCategories();
    }

    @Operation(summary = "Create or update a category", tags = {"Categories"})
    @PostMapping
    public void createOrUpdateCategory(@RequestBody Category category) {
        if (StringUtils.isEmpty(category.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID is required");
        }

        if (StringUtils.isEmpty(category.getGuidance())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category guidance is required");
        }

        logger.info("Creating/updating category with ID: {}", category.getId());

        this.categoryRepository.save(category);
    }

    @Operation(summary = "Get all categories", tags = {"Categories"})
    @GetMapping
    public List<Category> getCategories() {
        return this.categoryRepository.findAll();
    }

    @Operation(summary = "Get the latest version of rules for a category by ID", tags = {"Categories"})
    @GetMapping("/{categoryId}/rules")
    public Matcher getCategoryRules(@PathVariable String categoryId) {
        CategoryRule categoryRule = this.categoryRuleRepository.findLatestByCategoryId(categoryId);

        if (categoryRule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category rules not found");
        }

        return categoryRule.getMatcher();
    }

    @Operation(summary = "Create or update rules for a category by ID", tags = {"Categories"})
    @PostMapping("/{categoryId}/rules")
    public void createOrUpdateCategoryRules(@PathVariable String categoryId, @RequestBody Matcher categoryRule) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID is required");
        }

        if (categoryRule == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category rules are required");
        }

        logger.info("Creating/updating rules for category with ID: {}", categoryId);

        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        CategoryRule entity = new CategoryRule();
        entity.setCategory(category);
        entity.setMatcher(categoryRule);

        this.categoryRuleRepository.save(entity);
    }

    @Operation(summary = "Get the history of rules for a category by ID", tags = {"Categories"})
    @GetMapping("/{categoryId}/rules/history")
    public List<Matcher> getCategoryRulesHistory(@PathVariable String categoryId) {
        return this.categoryRuleRepository.findByCategoryId(categoryId).stream()
                .sorted(Comparator.comparing(CategoryRule::getTimestamp, Comparator.reverseOrder()))
                .map(CategoryRule::getMatcher)
                .toList();
    }

    @Operation(summary = "Bulk save categories and their rules", tags = {"Categories"})
    @PostMapping("/bulk")
    @Transactional
    public void bulkSaveCategories(@RequestBody List<CategorySnapshot> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categories are required");
        } else if (categories.stream().anyMatch(category -> StringUtils.isEmpty(category.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required for all categories");
        }

        boolean hasDuplicateIds = categories.stream()
                .collect(Collectors.groupingBy(CategorySnapshot::getId, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);

        if (hasDuplicateIds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate category IDs are not allowed");
        }

        boolean hasMissingRules = categories.stream().anyMatch(category -> category.getMatcher() == null);

        if (hasMissingRules) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rules are required for all categories");
        }

        logger.info("Bulk saving {} categories", categories.size());

        for (CategorySnapshot category : categories) {
            Category categoryEntity = this.categoryRepository.save(category.toCategory());
            this.categoryRuleRepository.save(category.toCategoryRule(categoryEntity));
        }
    }

    @Operation(summary = "Delete a category by ID", tags = {"Categories"})
    @DeleteMapping("/{categoryId}")
    @Transactional
    public void deleteCategory(@PathVariable String categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        this.categoryRuleRepository.deleteByCategoryId(categoryId);
        this.categoryRepository.deleteById(categoryId);
    }
}
