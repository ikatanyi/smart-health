package io.smarthealth.clinical.documents.domain.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.documents.domain.PatientDiagnosis;
import static io.smarthealth.infrastructure.utility.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import io.smarthealth.clinical.visit.validation.constraints.CheckValidVisit;
import org.smarthealth.patient.validation.constraints.ValidIdentifier;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Diagnosis {

    public enum Certainty {
        Confirmed,
        Presumed
    }

    public enum Order {
        Primary,
        Secondary
    }
    private Long id;

    @ValidIdentifier
    private String patientNumber;
    @CheckValidVisit
    private String visitNumber;

    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private Certainty certainty;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private Order diagnosisOrder;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime recorded = LocalDateTime.now();

    public static PatientDiagnosis map(Diagnosis diagnosis) {
        PatientDiagnosis entity = new PatientDiagnosis();
        entity.getDiagnosis().setCode(diagnosis.getCode());
        entity.getDiagnosis().setDescription(diagnosis.getDescription());
        entity.setCertainty(diagnosis.getDiagnosisCertainty());
        entity.setDiagnosisOrder(diagnosis.getDiagnosisOrder());
        return entity;
    }

    public static Diagnosis map(PatientDiagnosis entity) {
        Diagnosis diagnos = new Diagnosis();
        diagnos.setId(entity.getId());
        diagnos.setPatientNumber(entity.getPatient().getPatientNumber());
        diagnos.setVisitNumber(entity.getVisit().getVisitNumber());
        diagnos.setCode(entity.getDiagnosis().getCode());
        diagnos.setDescription(entity.getDiagnosis().getDescription());
        diagnos.setCertainty(Certainty.valueOf(entity.getCertainty()));
        diagnos.setDiagnosisOrder(Order.valueOf(entity.getDiagnosisOrder()));
        return diagnos;
    }

    public String getDiagnosisCertainty() {
        return certainty != null ? certainty.name() : "";
    }

    public String getDiagnosisOrder() {
        return diagnosisOrder != null ? diagnosisOrder.name() : "";
    }
}
