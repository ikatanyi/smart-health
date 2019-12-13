package io.smarthealth.clinical.record.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  Patient Consultation notes based on SOAP format
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_soap_notes")
public class PatientSoapNote extends ClinicalRecord{

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
}
