/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.ivorydata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon.waweru
 */
public class IvoryHistoricalClinicalDataSindano {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    DBConnector connector = new DBConnector();

    public static void main(String[] args) {
        IvoryHistoricalClinicalDataSindano sindano = new IvoryHistoricalClinicalDataSindano();
        sindano.processData();
    }

    private void processData() {

        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients

        Connection conn = null;
        try {
            conn = connector.ConnectToPastDB();
            String fetchPatientData = "SELECT e.pv_Entity_No, e.v_Fname,e.v_Mname, e.v_Lname, e.v_Id_No, e.d_Dob,e.d_Dor, e.v_File_No, e.d_Status_Date FROM dbo.m_Entity  AS e WHERE e.fv_Entity_Type_No = 'PAT'";
            pst = conn.prepareStatement(fetchPatientData);
            rs = pst.executeQuery();
            while (rs.next()) {
                PatientData data = new PatientData();
                data.setDDOB(rs.getDate("d_Dob"));
                data.setDDor(rs.getDate("d_Status_Date"));
                data.setFvEntityTypeNo("fv_Entity_Type_No");
                data.setPvEntityNo(rs.getString("pv_Entity_No"));
                data.setVFileNo(rs.getString("v_File_No"));
                data.setVFname(rs.getString("v_Fname"));
                data.setVLname(rs.getString("v_Lname"));
                data.setVMname(rs.getString("v_Mname"));

                //harmonize patient numbers for each patient
                data.setCurrentPatientNo(harmonizePatientNo(data));

                patients.add(data);
            }
            //  printMissingPatients(patients, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //create one patient visit for all the past visits
        try {
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //check if visit exists
                    String checkVisit = "SELECT id FROM smarthealth.patient_visit WHERE visit_number = '" + "VST-".concat(d.getPvEntityNo()) + "'";
                    pst2 = conn.prepareStatement(checkVisit);
                    rs = pst2.executeQuery();
                    if (!rs.next()) {
                        System.out.println("START: Insert smathealth patient_visit VST-".concat(d.getPvEntityNo()));
                        String visitRecord = "INSERT INTO smarthealth.patient_visit (created_by, created_on, last_modified_by, last_modified_on, version, comments, is_active_on_consultation, payment_method, scheduled, service_type, start_datetime, status, stop_datetime, triage_category, visit_number, visit_type, clinic_id, health_provider, patient_id, service_point_id) VALUES ('system', NOW(), 'system', NOW(), '1', 'VISIT B/U', b'0', 'Cash', b'0', 'Consultation', '2020-01-01 00:00:00.000000', 'CheckOut',  '2020-01-01 00:00:00.000000', '3', '" + "VST-".concat(d.getPvEntityNo()) + "', 'Outpatient', '1', NULL, (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), NULL)";
                        pst = conn.prepareStatement(visitRecord);
                        pst.executeUpdate();
                    }

                }
            }
//            pst.executeBatch();

            insertTriage(patients, conn);
            System.out.println("Done inserting vitals");
            insertDoctorNotes(patients, conn);
            System.out.println("Done inserting doctor notes");
            insertHistoricalPatientDiagnosis(patients, conn);
            System.out.println("Done inserting diagnosis");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertTriage(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent triage data
                    String triageHistoryNote = "SELECT d_Checked_Date,d_Checked_Date,v_BP,v_Height,v_Pulse_Rate,v_BP,v_Temperature,v_Weight  FROM dbo.m_triage WHERE pfv_Entity_No = '" + d.getPvEntityNo() + "'";
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

                            String triageNote = "INSERT INTO smarthealth.patient_vitals_record (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, bmi, category, comments, diastolic, height, pulse, spo2, systolic, temp, weight, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("d_Checked_Date") + "', b'0', NULL, NULL, 'VISIT B/U', '" + dia + "',  '" + rs.getString("v_Height") + "', '" + rs.getString("v_Pulse_Rate") + "',NULL, '" + sys + "', '" + rs.getString("v_Temperature") + "', '" + rs.getString("v_Weight") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
                            pst = conn.prepareStatement(triageNote);
                            //  pst.addBatch();
                            pst.execute();

                        } catch (Exception e) {
                            System.out.println("error " + e.getMessage());
                        }

                    }
                    // pst.executeBatch();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDoctorNotes(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent triage data
                    String historicalClinicalNotes = "SELECT pd_Doctor_Date, v_Doctor_Comments,v_Remarks   FROM dbo.t_doctor WHERE pfv_Patient_No = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(historicalClinicalNotes);
                    rs = pst2.executeQuery();
                    while (rs.next()) {
                        String clinicalNotes = "";
                        try {
                            clinicalNotes = "INSERT INTO smarthealth.patient_clinical_notes (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, chief_complaint, examination_notes, health_provider_id, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("pd_Doctor_Date") + "', b'0', '" + rs.getString("v_Doctor_Comments").replace("'", "''") + "', '" + rs.getString("v_Remarks").replace("'", "''") + "', NULL, (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
                            pst = conn.prepareStatement(clinicalNotes);
                            pst.execute();

                        } catch (Exception e) {

                            System.out.println("error " + e.getMessage());
                            System.out.println("clinicalNotes " + clinicalNotes);
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertHistoricalPatientDiagnosis(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent triage data
                    String historicalClinicalNotes = "SELECT pd_Doctor_Date, v_Doctor_Comments,v_Remarks , fv_Diagnosis  FROM dbo.t_doctor WHERE pfv_Patient_No = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(historicalClinicalNotes);
                    rs = pst2.executeQuery();
                    while (rs.next()) {
                        String diagnosis = "";
                        try {
                            diagnosis = "INSERT INTO smarthealth.patient_diagnosis (created_by, created_on, last_modified_by, last_modified_on, version, voided, certainty,date_recorded, code, description, diagnosis_order, notes, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', b'0', NULL, '" + rs.getString("pd_Doctor_Date") + "', NULL, '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', 'Primary', '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
                            pst = conn.prepareStatement(diagnosis);
                            pst.execute();

                        } catch (Exception e) {
                            System.out.println("error " + e.getMessage());
                            System.out.println("clinicalNotes " + diagnosis);
                        }

                    }
                    // pst.executeBatch();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String harmonizePatientNo(PatientData data) {
        String newPatientNo = "";

        DateFormat dateFormat = new SimpleDateFormat("yy");
        String yy = dateFormat.format(data.getDDor());

        newPatientNo = newPatientNo.concat("IHSL-").concat(data.getPvEntityNo()).concat("-").concat(yy);
        // System.out.println("newPatientNo " + newPatientNo);
        //check if missing 

        return newPatientNo;
    }

    private boolean patientAvailable(String patientNumber, Connection connection) throws Exception {
        try {
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + patientNumber + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating patient number", e);
        }
    }

    private void printMissingPatients(List<PatientData> data, Connection connection) {
        try {
            int count = 0;
            for (PatientData d : data) {
                //check if exists
                String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'";
                pst = connection.prepareStatement(validPatientNo);
                rs = pst.executeQuery();
                if (!rs.next()) {
                    System.out.println(d.getVFname().concat(" ").concat(d.getVMname()).concat(" ").concat(d.getVLname()) + " " + d.getCurrentPatientNo());
                    count++;
                }
            }
            System.out.println("Total count missing " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
