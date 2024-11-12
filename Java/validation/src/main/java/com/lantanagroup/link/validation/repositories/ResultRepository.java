package com.lantanagroup.link.validation.repositories;

import com.lantanagroup.link.validation.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    void deleteByTenantId(String tenantId);

    void deleteByTenantIdAndReportId(String tenantId, String reportId);
}
