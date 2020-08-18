/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.vimakdata;

import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.smarthealth.clinical.visit.data.VisitDatas;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 *
 * @author Simon.waweru
 */
@Component
public class VimakHistoricalClinicalData {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    DBConnector connector = new DBConnector();

    public static void main(String[] args) {
        VimakHistoricalClinicalData sindano = new VimakHistoricalClinicalData();
        sindano.insertDoctorNotes();
    }

    private void processData() {
        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients

        Connection conn = null;
        //create one patient visit for all the past visits
        try {
            conn = connector.ConnectToPastDB();
            //check if visit exists
            String checkVisit = "SELECT * FROM clinic_web.dt_psession";
            pst2 = conn.prepareStatement(checkVisit);
            rs = pst2.executeQuery();
            while (rs.next()) {
                System.out.println("START: Insert smathealth patient_visit VST-".concat(rs.getString("code")));

            }
//            pst.executeBatch();

//            insertTriage(patients, conn);
//            System.out.println("Done inserting vitals");
//            insertDoctorNotes(patients, conn);
//            System.out.println("Done inserting doctor notes");
//            insertHistoricalPatientDiagnosis(patients, conn);
//            System.out.println("Done inserting diagnosis");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertTriage(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                //find equivalent triage data
                String triageHistoryNote = "SELECT d_Checked_Date,d_Checked_Date,v_BP,v_Height,v_Pulse_Rate,v_BP,v_Temperature,v_Weight  FROM dbo.m_triage WHERE pfv_Entity_No = ''";
                pst2 = conn.prepareStatement(triageHistoryNote);
                rs = pst2.executeQuery();
                while (rs.next()) {

                    try {
                        String dia = null;
                        String sys = null;

                        try {
                            String names = rs.getString("v_BP").replace("/", ",");
                            String[] bpList = names.split(",");
                            dia = bpList[1];
                            sys = bpList[0];
                        } catch (Exception e) {
                            System.out.println("Systolic Diastolic manipulation error");
                        }

                        String triageNote = "INSERT INTO smarthealth.patient_vitals_record (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, bmi, category, comments, diastolic, height, pulse, spo2, systolic, temp, weight, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("d_Checked_Date") + "', b'0', NULL, NULL, 'VISIT B/U', '" + dia + "',  '" + rs.getString("v_Height") + "', '" + rs.getString("v_Pulse_Rate") + "',NULL, '" + sys + "', '" + rs.getString("v_Temperature") + "', '" + rs.getString("v_Weight") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = ''), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-";
                        pst = conn.prepareStatement(triageNote);
                        //  pst.addBatch();
                        pst.execute();

                    } catch (Exception e) {
                        System.out.println("error " + e.getMessage());
                    }

                    // pst.executeBatch();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDoctorNotes() {
        try {
            //insert triage history
            Connection connection = connector.ConnectToPastDB();
            Connection connection2 = connector.ConnectToCurrentDB();
            //find equivalent triage data
            String historicalClinicalNotes = "SELECT * FROM clinic_web.dt_psession";
            pst2 = connection.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                String clinicalNotes = "";
                String examination_notes = rs.getString("doctor_notes").trim();
                String chief_complaint = null;
                LocalDate toDt = LocalDate.parse(rs.getString("date"));
                Integer visitId = getVisit(rs.getString("visit_code"), rs.getString("patient_id"), toDt);
                PatientData pdata = patient(rs.getString("patient_id"));
                Long patientId = pdata != null ? pdata.getId() : 0;
                String comments = "";
                String remarks = "";

                LocalDateTime dtTime = toDt.atStartOfDay();
                try {
                    clinicalNotes = "INSERT INTO smarthealth.patient_clinical_notes (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, chief_complaint, examination_notes, health_provider_id, patient_id, visit_id) "
                            + "VALUES ('system', '" + dtTime + "', 'system', '" + dtTime + "', '0', '" + toDt + "', b'0', '" + chief_complaint + "', '" + examination_notes + "', NULL, '" + patientId + "', '" + (visitId == null ? 0 : visitId) + "')";
                    pst = connection2.prepareStatement(clinicalNotes);
                    pst.execute();
                    System.out.println("Processed visitId" + visitId);
                } catch (Exception e) {

                    System.out.println("error " + e.getMessage());
                    System.out.println("clinicalNotes " + clinicalNotes);
                }

            }
            // pst.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertHistoricalPatientDiagnosis(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            //find equivalent triage data
            String historicalClinicalNotes = "SELECT *  FROM clinic_web.dt_psession";
            pst2 = conn.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                String diagnosis = "";
                try {
                    diagnosis = "INSERT INTO smarthealth.patient_diagnosis (created_by, created_on, last_modified_by, last_modified_on, version, voided, certainty,date_recorded, code, description, diagnosis_order, notes, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', b'0', NULL, '" + rs.getString("pd_Doctor_Date") + "', NULL, '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', 'Primary', '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = ), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-";
                    pst = conn.prepareStatement(diagnosis);
                    pst.execute();

                } catch (Exception e) {
                    System.out.println("error " + e.getMessage());
                    System.out.println("clinicalNotes " + diagnosis);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PatientData patient(String patientNumber) throws Exception {
        try {
            PatientData data = null;
            ResultSet rs = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + patientNumber + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                data = new PatientData();
                data.setPatientNumber(rs.getString("patient_number"));
                data.setId(rs.getLong("id"));
            }
            connection.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating patient number", e);
        }
    }

    private int getVisit(String visitNumber, String patientNumber, LocalDate toDate) throws Exception {
        try {
            VisitDatas data = null;
            ResultSet rs = null;
            int visitId = 0;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient_visit WHERE visit_number = '" + visitNumber + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                visitId = rs.getInt("id");
            } else {
                PatientData patient = patient(patientNumber);
                if (patient != null) {
                    Long patientId = patient.getId();
                    String code = visitNumber;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                    LocalDate toDate = LocalDate.parse(rs.getString("date"));
                    LocalDateTime dateTime = toDate.atStartOfDay();
                    String visitRecord = "INSERT INTO smarthealth.patient_visit (created_by, created_on, last_modified_by, last_modified_on, version, comments, is_active_on_consultation, payment_method, scheduled, service_type, start_datetime, status, stop_datetime, triage_category, visit_number, visit_type, clinic_id, health_provider, patient_id, service_point_id) "
                            + "VALUES ('system', '" + dateTime + "', 'system', '" + dateTime + "', '1', 'VISIT B/U', b'0', 'Cash', b'0', 'Consultation', '" + dateTime + "', 'CheckOut',  '" + dateTime + "', '3', '" + code + "', 'Outpatient', '1', NULL, '" + patientId + "', NULL)";
                    pst = connection.prepareStatement(visitRecord, Statement.RETURN_GENERATED_KEYS);

                    pst.executeUpdate();
                    ResultSet rs1 = pst.getGeneratedKeys();
                    if (rs1.next()) {
                        int i = rs1.getInt(1);
                        visitId = i;
                    }
                }
            }
            connection.close();
            return visitId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating visit number", e);
        }
    }

    private Long getUser(String userId) throws Exception {
        try {
            VisitDatas data = null;
            ResultSet rs = null;
            Long id = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.dt_users WHERE IdNo = '" + userId + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
            connection.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating visit number", e);
        }
    }

//    private void printMissingPatients(List<PatientData> data, Connection connection) {
//        try {
//            int count = 0;
//            for (PatientData d : data) {
//                //check if exists
//                String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'";
//                pst = connection.prepareStatement(validPatientNo);
//                rs = pst.executeQuery();
//                if (!rs.next()) {
//                    System.out.println(d.getVFname().concat(" ").concat(d.getVMname()).concat(" ").concat(d.getVLname()) + " " + d.getCurrentPatientNo());
//                    count++;
//                }
//            }
//            System.out.println("Total count missing " + count);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
