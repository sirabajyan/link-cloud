package com.lantanagroup.link.validation.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lantanagroup.link.validation.entities.CategoryEntity;
import com.lantanagroup.link.validation.models.BulkSaveCategoryModel;
import com.lantanagroup.link.validation.repositories.CategoryRepository;
import com.lantanagroup.link.validation.repositories.CategoryRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class CategorizationService {
    private static final Logger logger = LoggerFactory.getLogger(CategorizationService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryRuleRepository categoryRuleRepository;

    public CategorizationService(CategoryRepository categoryRepository, CategoryRuleRepository categoryRuleRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryRuleRepository = categoryRuleRepository;
    }

    public void initCategories() {
        logger.info("Initializing categories");
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream stream = ClassLoader.getSystemResourceAsStream("categories.json")) {
            BulkSaveCategoryModel[] categories = mapper.readValue(stream, BulkSaveCategoryModel[].class);
            for (BulkSaveCategoryModel category : categories) {
                logger.debug("Initializing category: {}", category.getId());
                CategoryEntity entity = categoryRepository.save(category.toEntity());
                categoryRuleRepository.save(category.toRuleEntity(entity));
            }
        } catch (Exception e) {
            logger.error("Failed to initialize categories", e);
            throw new RuntimeException(e);
        }
    }
}
