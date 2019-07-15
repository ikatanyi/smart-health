package io.smarthealth.clinical.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Consultation Doctor's patient notes
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_clinical_notes")
public class PatientNotes extends ClinicalRecord{

    private String chiefComplaint;
    private String historyNotes; //history of present complaints
    private String examinationNotes;
    private String socialHistory;
}
