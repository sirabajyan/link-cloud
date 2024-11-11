package com.lantanagroup.link.validation.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import com.lantanagroup.link.validation.entities.ResultEntity;
import com.lantanagroup.link.validation.models.ResultModel;
import com.lantanagroup.link.validation.repositories.ResultRepository;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequestScope
public class ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    private final FhirContext fhirContext;
    private final ArtifactService artifactService;
    private final ResultRepository resultRepository;
    private FhirValidator validator;
    private PrePopulatedValidationSupport prePopulatedValidationSupport;

    public ValidationService(
            FhirContext fhirContext,
            ArtifactService artifactService,
            ResultRepository resultRepository) {
        this.fhirContext = fhirContext;
        this.artifactService = artifactService;
        this.resultRepository = resultRepository;

        initArtifacts();
    }

    private void initArtifacts() {
        logger.info("Loading artifacts");

        ValidationSupportChain validationSupportChain = new ValidationSupportChain(
                new DefaultProfileValidationSupport(this.fhirContext),
                new InMemoryTerminologyServerValidationSupport(this.fhirContext),
                this.artifactService.getValidationSupport()
        );
        this.validator = this.fhirContext.newValidator();
        this.validator.setExecutorService(ForkJoinPool.commonPool());
        IValidatorModule module = new FhirInstanceValidator(new CachingValidationSupport(validationSupportChain));
        this.validator.registerValidatorModule(module);
        this.validator.setConcurrentBundleValidation(true);

        logger.info("Done loading artifacts into validator");
    }

    public List<ResultModel> validate(Resource resource) {
        logger.info("Validating resource");

        ValidationResult validationResult = this.validator.validateWithResult(resource);
        return validationResult.getMessages().stream().map(issue -> {
            ResultModel result = new ResultModel();
            result.setMessage(issue.getMessage());
            result.setExpression(issue.getLocationString());
            result.setSeverity(getIssueSeverity(issue.getSeverity()));
            result.setCode(getIssueCode(issue.getMessageId()));

            if (issue.getLocationLine() != null && issue.getLocationCol() != null) {
                result.setLocation(String.format("%d:%d", issue.getLocationLine(), issue.getLocationCol()));
            }

            return result;
        }).toList();
    }

    public OperationOutcome convertToOperationOutcome(List<ResultModel> results) {
        OperationOutcome operationOutcome = new OperationOutcome();

        results.forEach(result -> {
            OperationOutcome.OperationOutcomeIssueComponent issue = operationOutcome.addIssue();
            issue.setDiagnostics(result.getMessage());
            issue.getExpression().add(new StringType(result.getExpression()));
            issue.setSeverity(result.getSeverity());
            issue.setCode(result.getCode());

            if (StringUtils.isNotEmpty(result.getLocation())) {
                issue.addLocation(result.getLocation());
            }
        });

        return operationOutcome;
    }

    public void saveResults(List<ResultModel> results, String tenantId, String reportId) {
        this.resultRepository.deleteByTenantIdAndReportId(tenantId, reportId);
        List<ResultEntity> entities = results.stream().map(ResultEntity::new).toList();
        this.resultRepository.saveAll(entities);
    }

    private static OperationOutcome.IssueSeverity getIssueSeverity(ResultSeverityEnum severity) {
        return switch (severity) {
            case ERROR -> OperationOutcome.IssueSeverity.ERROR;
            case WARNING -> OperationOutcome.IssueSeverity.WARNING;
            case INFORMATION -> OperationOutcome.IssueSeverity.INFORMATION;
            case FATAL -> OperationOutcome.IssueSeverity.FATAL;
            default -> throw new RuntimeException("Unexpected severity " + severity);
        };
    }

    private static OperationOutcome.IssueType getIssueCode(String messageId) {
        if (messageId == null) {
            return OperationOutcome.IssueType.NULL;
        } else if (messageId.startsWith("Rule ")) {
            return OperationOutcome.IssueType.INVARIANT;
        }

        return switch (messageId) {
            case "TERMINOLOGY_TX_SYSTEM_NO_CODE" -> OperationOutcome.IssueType.INFORMATIONAL;
            case "Terminology_TX_NoValid_2_CC", "Terminology_PassThrough_TX_Message",
                 "Terminology_TX_Code_ValueSet_Ext", "Terminology_TX_NoValid_17", "Terminology_TX_NoValid_16",
                 "Terminology_TX_NoValid_3_CC" -> OperationOutcome.IssueType.CODEINVALID;
            case "Extension_EXT_Unknown" -> OperationOutcome.IssueType.EXTENSION;
            case "Measure_MR_M_NotFound" -> OperationOutcome.IssueType.NOTFOUND;
            case "Validation_VAL_Profile_Minimum", "Validation_VAL_Profile_Maximum", "Extension_EXT_Type",
                 "Validation_VAL_Profile_Unknown", "Reference_REF_NoDisplay" -> OperationOutcome.IssueType.STRUCTURE;
            case "Type_Specific_Checks_DT_String_WS" -> OperationOutcome.IssueType.VALUE;
            case "Terminology_TX_System_Unknown" -> OperationOutcome.IssueType.UNKNOWN;
            case "Type_Specific_Checks_DT_Code_WS" -> OperationOutcome.IssueType.INVALID;
            default -> OperationOutcome.IssueType.NULL;
        };
    }
}
