package com.lantanagroup.link.validation.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lantanagroup.link.validation.model.ArtifactType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "artifact")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtifactType type;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "varbinary(max)", nullable = false)
    private byte[] content;
}
