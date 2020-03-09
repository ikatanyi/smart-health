/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.record.data.DiagnosisData;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientVisitData {

    private String title;
    private String dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String patientId;
    
    private String addressLine1;
    private String addressLine2;
    private String addressTown;
    private String addressCounty;
    private String addressPostalCode;
    private String addressCountry;
    
    private String ContactEmail;
    private String ContactTelephone;
    private String contactMobile;
    
    private String fullName;
    private String visitNumber;
    private String createdOn;
    private String practitionerName;
    
    private String chiefComplaint;
    private String historyNotes; //history of present complaints
    private String examinationNotes;
    private String socialHistory;
    private String briefNotes;
    private Integer age;
    
    List<PatientScanRegisterData> radiologyTests;
    List<PatientProcedureRegisterData> procedures;
    List<LabResultData> labTests;
    List<DiagnosisData> diagnosis;
    List<PatientDrugsData> drugsData;
}
