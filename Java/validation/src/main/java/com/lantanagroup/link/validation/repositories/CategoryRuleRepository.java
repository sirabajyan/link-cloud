package com.lantanagroup.link.validation.repositories;

import com.lantanagroup.link.validation.entities.CategoryRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRuleRepository extends JpaRepository<CategoryRuleEntity, Long> {
    List<CategoryRuleEntity> findByCategoryId(String categoryId);

    @Query(
            value = """
                    SELECT TOP 1 *
                    FROM category_rule
                    WHERE category_id = :categoryId
                    ORDER BY timestamp DESC;""",
            nativeQuery = true)
    CategoryRuleEntity getLatestByCategoryId(String categoryId);

    void deleteByCategoryId(String categoryId);
}
