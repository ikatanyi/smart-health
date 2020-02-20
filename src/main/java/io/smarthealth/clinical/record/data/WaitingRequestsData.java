/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.organization.person.patient.data.PatientData;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class WaitingRequestsData {

    private Long requestId;
    private String patientNumber;
    private String visitNumber;
    private VisitData visitData;
    private PatientData patientData;
    private String requestedByName, requestedByNo;
    private List<DoctorRequestItem> item = new ArrayList<>();

}
