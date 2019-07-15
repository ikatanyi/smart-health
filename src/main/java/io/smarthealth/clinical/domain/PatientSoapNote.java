package io.smarthealth.clinical.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

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
