package com.lantanagroup.link.validation.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.LenientErrorHandler;
import com.lantanagroup.link.validation.config.ArtifactConfig;
import com.lantanagroup.link.validation.entities.ArtifactEntity;
import com.lantanagroup.link.validation.model.ArtifactType;
import com.lantanagroup.link.validation.repositories.ArtifactRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class ArtifactService {
    private static final Logger log = LoggerFactory.getLogger(ArtifactService.class);
    private static final List<String> allowedResourceTypes = List.of("StructureDefinition", "ValueSet", "CodeSystem");

    private final FhirContext fhirContext;
    private final ArtifactRepository repository;
    private PrePopulatedValidationSupport validationSupport;

    public ArtifactService(
            FhirContext fhirContext,
            ArtifactRepository repository,
            ArtifactConfig artifactConfig) {
        this.fhirContext = fhirContext;
        this.repository = repository;

        if (artifactConfig.isInit()) {
            this.initArtifacts();
        } else {
            log.info("Skipping artifact initialization due to configuration");
        }
    }

    public void createOrUpdateArtifact(String name, ArtifactType type, byte[] content) {
        List<ArtifactEntity> artifactEntities = this.repository.findByTypeAndName(type, name);

        if (artifactEntities.size() > 1) {
            throw new RuntimeException("Multiple artifacts found with the same name");
        } else if (artifactEntities.size() == 1) {
            ArtifactEntity artifactEntity = artifactEntities.get(0);
            artifactEntity.setContent(content);
            this.repository.save(artifactEntity);
            invalidateValidationSupport();
        } else {
            ArtifactEntity artifactEntity = new ArtifactEntity();
            artifactEntity.setType(type);
            artifactEntity.setName(name);
            artifactEntity.setContent(content);
            this.repository.save(artifactEntity);
            invalidateValidationSupport();
        }
    }

    public void deleteArtifact(ArtifactType type, String name) {
        List<ArtifactEntity> artifactEntities = this.repository.findByTypeAndName(type, name);
        if (artifactEntities.size() > 1) {
            throw new RuntimeException("Multiple artifacts found with the same name");
        } else if (artifactEntities.size() == 1) {
            this.repository.delete(artifactEntities.get(0));
            invalidateValidationSupport();
        }
    }

    public List<ArtifactEntity> listArtifacts() {
        return this.repository
                .findAll()
                .stream().map(artifactEntity -> {
                    artifactEntity.setContent(null);
                    return artifactEntity;
                }).toList();
    }

    public List<ArtifactEntity> getArtifacts() {
        return this.repository.findAll();
    }

    public void initArtifacts() {
        this.initArtifacts(ArtifactType.PACKAGE);
        this.initArtifacts(ArtifactType.RESOURCE);
    }

    private void initArtifacts(ArtifactType type) {
        List<String> extensions = type == ArtifactType.RESOURCE ? List.of("json", "xml") : List.of("tgz");
        String path = type == ArtifactType.RESOURCE ? "classpath:/resources/**" : "classpath:/packages/**";
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources;

        try {
            resources = resolver.getResources(path);
        } catch (IOException e) {
            log.error("Error initializing artifacts for type {} from path {} in class resources", type, path, e);
            throw new RuntimeException("Error initializing artifacts");
        }

        for (org.springframework.core.io.Resource resourceResource : resources) {
            String extension = Objects.requireNonNull(resourceResource.getFilename())
                    .substring(resourceResource.getFilename().lastIndexOf(".") + 1)
                    .toLowerCase();
            String fileName = new File(resourceResource.getFilename())
                    .getName();

            if (StringUtils.isEmpty(fileName) || StringUtils.isEmpty(extension)) {
                continue;
            } else if (!extensions.contains(extension.toLowerCase())) {
                log.warn("Unexpected file name {} for type {} in class resources", resourceResource.getFilename(), type);
                continue;
            }

            // Remove the file extension and remove the "CodeSystem-" or "ValueSet-" prefix
            fileName = fileName.substring(0, resourceResource.getFilename().lastIndexOf("."))
                    .replaceFirst("^(CodeSystem|ValueSet)-", "");

            log.info("Loading resource {}", resourceResource.getFilename());

            try {
                byte[] resourceContent = resourceResource.getContentAsByteArray();

                if (this.repository.findByTypeAndName(type, fileName).isEmpty()) {
                    ArtifactEntity artifactEntity = new ArtifactEntity();
                    artifactEntity.setType(type);
                    artifactEntity.setName(fileName);
                    artifactEntity.setContent(resourceContent);
                    this.repository.save(artifactEntity);
                    invalidateValidationSupport();
                }
            } catch (IOException e) {
                log.error("Error get content for resource in class resources {}", resourceResource.getFilename(), e);
            }
        }
    }

    private synchronized void invalidateValidationSupport() {
        this.validationSupport = null;
    }

    public synchronized IValidationSupport getValidationSupport() {
        if (this.validationSupport == null) {
            this.validationSupport = new PrePopulatedValidationSupport(this.fhirContext);
            for (ArtifactEntity artifactEntity : this.getArtifacts()) {
                switch (artifactEntity.getType()) {
                    case PACKAGE -> this.loadPackage(artifactEntity);
                    case RESOURCE -> this.loadResource(artifactEntity);
                }
            }
        }
        return this.validationSupport;
    }

    private synchronized void loadPackage(ArtifactEntity artifactEntity) {
        if (artifactEntity.getType() != ArtifactType.PACKAGE) {
            throw new RuntimeException("Artifact is not an NPM package");
        }

        log.info("Loading package into validation support: {}", artifactEntity.getName());

        try (InputStream stream = new ByteArrayInputStream(artifactEntity.getContent())) {
            NpmPackage npmPackage = NpmPackage.fromPackage(stream);
            List<String> resourceNames = npmPackage.listResources(allowedResourceTypes);

            for (String resourceName : resourceNames) {
                log.debug("Loading resource from package {}: {}", artifactEntity.getName(), resourceName);
                try (InputStream resourceContent = npmPackage.loadResource(resourceName)) {
                    this.loadResource(resourceContent, resourceName);
                } catch (IOException | DataFormatException e) {
                    log.warn("Error loading resource from package {}: {}", artifactEntity.getName(), resourceName, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading package", e);
        }
    }

    private synchronized void loadResource(ArtifactEntity artifactEntity) {
        if (artifactEntity.getType() != ArtifactType.RESOURCE) {
            throw new RuntimeException("Artifact is not a resource");
        }

        log.info("Loading resource into validation support: {}", artifactEntity.getName());

        try (InputStream stream = new ByteArrayInputStream(artifactEntity.getContent())) {
            this.loadResource(stream, artifactEntity.getName());
        } catch (IOException | DataFormatException e) {
            log.warn("Error loading resource {}", artifactEntity.getName(), e);
        }
    }

    private synchronized void loadResource(InputStream stream, String name) {
        // Remove HTML comments before parsing
        // This avoids an error that occurs when:
        //   - The CQF tooling emits raw CQL in Library.text (as an HTML comment)
        //   - The CQL contains the string "--" (which is invalid in an HTML comment)
        String json;
        try {
            json = IOUtils.toString(stream, StandardCharsets.UTF_8)
                    .replaceAll("<!--.+?-->", "");
        } catch (IOException e) {
            log.error("Error reading resource {}", name, e);
            return;
        }

        try {
            IParser parser = this.fhirContext.newJsonParser();
            parser.setParserErrorHandler(new LenientErrorHandler(false));
            IBaseResource resource = parser.parseResource(json);
            this.validationSupport.addResource(resource);
        } catch (DataFormatException e) {
            log.warn("Error loading resource {} with starting content {}", name, StringUtils.truncate(json, 100), e);
        }
    }
}
