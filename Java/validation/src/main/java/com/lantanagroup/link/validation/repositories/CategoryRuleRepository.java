package com.lantanagroup.link.validation.repositories;

import com.lantanagroup.link.validation.entities.CategoryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRuleRepository extends JpaRepository<CategoryRule, Long> {
    List<CategoryRule> findByCategoryId(String categoryId);

    @Query(
            value = """
                    SELECT TOP 1 *
                    FROM category_rule
                    WHERE category_id = :categoryId
                    ORDER BY timestamp DESC;""",
            nativeQuery = true)
    CategoryRule findLatestByCategoryId(String categoryId);

    void deleteByCategoryId(String categoryId);
}
