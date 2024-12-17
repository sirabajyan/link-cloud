package com.lantanagroup.link.validation.controllers;

import ca.uhn.fhir.context.FhirContext;
import com.lantanagroup.link.validation.entities.Category;
import com.lantanagroup.link.validation.entities.Result;
import com.lantanagroup.link.validation.services.CategorizationService;
import com.lantanagroup.link.validation.services.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/validation")
@SecurityRequirement(name = "bearer-key")
public class ValidationController {
    private final FhirContext fhirContext;
    private final ValidationService validationService;
    private final CategorizationService categorizationService;

    public ValidationController(
            FhirContext fhirContext,
            ValidationService validationService,
            CategorizationService categorizationService) {
        this.fhirContext = fhirContext;
        this.validationService = validationService;
        this.categorizationService = categorizationService;
    }

    @Operation(summary = "Validates a FHIR resource")
    @PostMapping("/$validate")
    public List<Result> validate(@RequestParam(defaultValue = "false") boolean categorize, @RequestBody String json) {
        if (StringUtils.isBlank(json)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No resource provided");
        }
        IBaseResource resource;
        try {
            resource = fhirContext.newJsonParser().parseResource(json);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Failed to parse resource: %s", e.getMessage()));
        }
        List<Result> results = validationService.validate(resource);
        if (categorize) {
            categorize(results);
        }
        return results;
    }

    @Operation(summary = "Categorizes validation results")
    @PostMapping("/$categorize")
    public List<Result> categorize(@RequestBody List<Result> results) {
        categorizationService.categorize(results);
        for (Result result : results) {
            if (result.getCategories().isEmpty()) {
                result.setCategories(List.of(Category.UNCATEGORIZED));
            }
        }
        return results;
    }
}
