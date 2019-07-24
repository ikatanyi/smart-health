package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import static io.smarthealth.infrastructure.utility.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import lombok.Data;
import io.smarthealth.clinical.visit.validation.constraints.CheckValidVisit;
import org.smarthealth.patient.validation.constraints.ValidIdentifier;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VitalRecordData {

    private Long id;
    @CheckValidVisit
    private String visitNumber;
    private String visitType;

    @ValidIdentifier
    private String patientNumber;

    private String patientName;
    private Float temp;
    private Float height;
    private Float weight;
    private Float bmi;
    private String category;
    private Float systolic;
    private Float diastolic;
    private Float pulse;
    private Float respiretory;
    private Float spo2;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime vitalsDatetime;

    public static VitalsRecord map(VitalRecordData triage) {
        VitalsRecord entity = new VitalsRecord();
        entity.setTemp(triage.getTemp());
        entity.setHeight(triage.getHeight());
        entity.setWeight(triage.getWeight());
        entity.setSystolic(triage.getSystolic());
        entity.setDiastolic(triage.getDiastolic());
        entity.setPulse(triage.getPulse());
        entity.setRespiretory(triage.getRespiretory());
        entity.setSpo2(triage.getSpo2());
        entity.setDateRecorded(triage.getVitalsDatetime());
        return entity;
    }

    public static VitalRecordData map(VitalsRecord entity) {
        VitalRecordData triage = new VitalRecordData();
        triage.setId(entity.getId());
        triage.setTemp(entity.getTemp());
        triage.setHeight(entity.getHeight());
        triage.setWeight(entity.getWeight());
        triage.setSystolic(entity.getSystolic());
        triage.setDiastolic(entity.getDiastolic());
        triage.setPulse(entity.getPulse());
        triage.setRespiretory(entity.getRespiretory());
        triage.setSpo2(entity.getSpo2());
        triage.setVitalsDatetime(entity.getDateRecorded());
        triage.setPatientNumber(entity.getPatient().getPatientNumber());
        triage.setVisitNumber(entity.getVisit().getVisitNumber());
        triage.setVisitType(entity.getVisit().getVisitType().name());
        triage.setCategory(entity.getCategory());
        triage.setBmi(entity.getBmi());
        triage.setPatientName(entity.getPatient().getSurname() + " " + entity.getPatient().getGivenName());
        return triage;
    }

}
