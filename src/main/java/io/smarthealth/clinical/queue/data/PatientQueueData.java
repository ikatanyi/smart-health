/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.data;

import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.person.patient.data.PatientData;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientQueueData {

    private String patientNumber;
    private String visitNumber;
    private String departmentId;

    private DepartmentData departmentData;
    private VisitData visitData;
    private PatientData patientData;
    private Long id;
    private String urgency;
    private String specialNotes;
    private  String servicePointName;
    

}
