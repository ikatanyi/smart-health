package io.smarthealth.clinical.record.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Patient Consultation Doctor's patient notes
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_clinical_notes")
public class PatientNotes extends ClinicalRecord {

    private String chiefComplaint;
    private String historyNotes; //history of present complaints
    private String examinationNotes;
    private String socialHistory;
}
