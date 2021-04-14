package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;

@Getter
@Setter
public class PatientPrescription {

    private Long id;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate prescriptionDate;
    private String prescribedBy;
    private Long doctorId;
    private String itemCode;
    private String itemName;
    private String medication;
    private String route;
    private String frequency;
    private Double dose;
    private String doseUnits;
    private Integer duration;
    private String durationUnits;
    private String instructions;
    private Integer numRefills;
    private Boolean onDischarge = false;


    public static PatientPrescription of(Prescription prescription){
        PatientPrescription data = new PatientPrescription();
        data.setId(prescription.getId());

        if (prescription.getPatient() != null) {
            data.setPatientName(prescription.getPatient().getGivenName());
            data.setPatientNumber(prescription.getPatientNumber());
        }
        if (prescription.getItem() != null) {
//            pd.setItemType(prescription.getItem().getDrugForm());
            data.setItemCode(prescription.getItem().getItemCode());
            data.setItemName(prescription.getItem().getItemName());
        } else {
            data.setItemName(prescription.getBrandName());
        }
        data.setMedication(prescription.getBrandName());
        data.setVisitNumber(prescription.getVisitNumber());
        data.setPrescriptionDate(prescription.getOrderDate().toLocalDate());
        if(prescription.getRequestedBy()!=null) {
            data.setPrescribedBy(prescription.getRequestedBy().getName());
            data.setDoctorId(prescription.getId());
        }
        data.setDose(prescription.getDose());
        data.setDoseUnits(prescription.getDoseUnits());
        data.setInstructions(prescription.getDosingInstructions());
        data.setDuration(prescription.getDuration());
        data.setDurationUnits(prescription.getDurationUnits());
        data.setFrequency(prescription.getFrequency());
        data.setRoute(prescription.getRoute());
        data.setNumRefills(prescription.getNumRefills());
        data.setOnDischarge(prescription.getOnDischarge());

       return data;
    }

    public String toFormattedString(){
        return StringUtils.capitalise(this.medication)
                .concat(" (" + StringUtils.clean(this.route) + ") Take " + this.dose + StringUtils.clean(this.doseUnits) + " " +
                         StringUtils.clean(this.frequency) + " " +
                         this.duration + " " + StringUtils.clean(this.durationUnits)+" \n");
    }
}
