package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.domain.Diagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosisData {

    public enum Certainty {
        Confirmed,
        Presumed
    }

    public enum Order {
        Primary,
        Secondary
    }
    private Long id;

    private String patientNumber;
    private String visitNumber;

    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private Certainty certainty;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private Order diagnosisOrder;

    private String notes;

    private PatientData patientData;
    @ApiModelProperty(required = false, hidden = true)
    private int age;
    private Boolean condition;

    public DiagnosisData() {
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime dateRecorded = LocalDateTime.now();

    public static PatientDiagnosis map(DiagnosisData diagnosis) {
        PatientDiagnosis entity = new PatientDiagnosis();
        Diagnosis diagnosis1 = new Diagnosis();
        diagnosis1.setCode(diagnosis.getCode());
        diagnosis1.setDescription(diagnosis.getDescription());
        entity.setDiagnosis(diagnosis1);
        entity.setCertainty(diagnosis.getCertainty() != null ? diagnosis.getCertainty().name() : null);
        entity.setDiagnosisOrder(diagnosis.getDiagnosisOrder() != null ? diagnosis.getDiagnosisOrder().name() : null);
        entity.setNotes(diagnosis.getNotes());
        entity.setDateRecorded(diagnosis.getDateRecorded());
        return entity;
    }

    public static DiagnosisData map(PatientDiagnosis entity) {
        DiagnosisData diagnos = new DiagnosisData();
        diagnos.setId(entity.getId());
        diagnos.setPatientNumber(entity.getPatient().getPatientNumber());
        diagnos.setVisitNumber(entity.getVisit().getVisitNumber());
        diagnos.setCode(entity.getDiagnosis().getCode());
        diagnos.setDescription(entity.getDiagnosis().getDescription());
        if (entity.getCertainty() != null) {
            diagnos.setCertainty(Certainty.valueOf(entity.getCertainty()));
        }
        diagnos.setDiagnosisOrder(Order.valueOf(entity.getDiagnosisOrder()));
        diagnos.setNotes(entity.getNotes());
        diagnos.setCondition(entity.getIsCondition());
        diagnos.setDateRecorded(entity.getDateRecorded());
        return diagnos;
    }

}
