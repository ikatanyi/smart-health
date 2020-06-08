package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.visit.validation.constraints.CheckValidVisit;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import org.smarthealth.patient.validation.constraints.ValidIdentifier;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientTestsData {

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
    @ApiModelProperty(required = false, hidden = true)
    private String patientName;
    @ApiModelProperty(required = false, hidden = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @CheckValidVisit
    private String visitNumber;
    
    @ApiModelProperty(required = false, hidden = true)
    private Integer age;

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
    @ApiModelProperty(required = false, hidden = true)
    private String doctor;

    public static PatientDiagnosis map(PatientTestsData diagnosis) {
        PatientDiagnosis entity = new PatientDiagnosis();
        entity.getDiagnosis().setCode(diagnosis.getCode());
        entity.getDiagnosis().setDescription(diagnosis.getDescription());
        entity.setCertainty(diagnosis.getCertainty()!=null ? diagnosis.getCertainty().name(): null);
        entity.setDiagnosisOrder(diagnosis.getDiagnosisOrder()!=null ? diagnosis.getDiagnosisOrder().name(): null);
        return entity;
    }

    public static PatientTestsData map(PatientDiagnosis entity) {
        PatientTestsData diagnos = new PatientTestsData();
        diagnos.setId(entity.getId());
        diagnos.setPatientNumber(entity.getPatient().getPatientNumber());
        diagnos.setVisitNumber(entity.getVisit().getVisitNumber());
        diagnos.setCode(entity.getDiagnosis().getCode());
        diagnos.setDescription(entity.getDiagnosis().getDescription());
        diagnos.setCertainty(Certainty.valueOf(entity.getCertainty()));
        diagnos.setDiagnosisOrder(Order.valueOf(entity.getDiagnosisOrder()));
        diagnos.setRecorded(entity.getDateRecorded());
        diagnos.setAge(entity.getPatient().getAge());
        diagnos.setGender(entity.getPatient().getGender());
        if(entity.getVisit().getPatient()!=null)
            diagnos.setPatientName(entity.getVisit().getPatient().getFullName());
        if(entity.getVisit().getHealthProvider()!=null)
           diagnos.setDoctor(entity.getVisit().getHealthProvider().getFullName());
        return diagnos;
    }

//    public String getDiagnosisCertainty() {
//        return certainty != null ? certainty.name() : "";
//    }
//
//    public String getDiagnosisOrder() {
//        return diagnosisOrder != null ? diagnosisOrder.name() : "";
//    }
}
