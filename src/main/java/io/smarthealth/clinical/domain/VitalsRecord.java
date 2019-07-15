package io.smarthealth.clinical.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Vital Records
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_vitals_record")
public class VitalsRecord extends ClinicalRecord {

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
    private String comments;
}
