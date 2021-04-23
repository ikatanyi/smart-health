package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.PatientPrescription;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DischargeSummaryReport extends DischargeData{

    private String proceduresSummary;
    private String diagnosisSummary;
    private String medicationsSummary;
    private String radiologySummary;
    private String laboratorySummary;
    private List<DiagnosisData> diagnosisList = new ArrayList<>();
    private List<PatientPrescription> prescriptionList = new ArrayList<>();

    public String getMedicationsSummary() {
        return prescriptionList.stream()
                .map(PatientPrescription::toFormattedString)
                .collect(Collectors.joining());
    }

    public String getDiagnosisSummary() {
      return  diagnosisList.stream()
                .map(DiagnosisData::toFormattedString)
                .collect(Collectors.joining());
    }
    public Long getDiagnosisId(){
        DiagnosisData dia= diagnosisList.stream().findFirst().orElse(null);
        if(dia==null) return null;

        return dia.getId();
    }
}
