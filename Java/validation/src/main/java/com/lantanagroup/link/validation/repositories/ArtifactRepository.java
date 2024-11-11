package com.lantanagroup.link.validation.repositories;

import com.lantanagroup.link.validation.entities.ArtifactEntity;
import com.lantanagroup.link.validation.models.ArtifactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtifactRepository extends JpaRepository<ArtifactEntity, Long> {
    List<ArtifactEntity> findByTypeAndName(ArtifactType type, String name);
}
