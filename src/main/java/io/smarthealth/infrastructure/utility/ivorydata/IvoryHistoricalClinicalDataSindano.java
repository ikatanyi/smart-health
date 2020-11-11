/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.ivorydata;

import com.nimbusds.openid.connect.sdk.claims.Gender;
//import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.domain.enumeration.MaritalStatus;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@RequiredArgsConstructor
@Service
public class IvoryHistoricalClinicalDataSindano {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    Connection conn = null;
    DBConnector connector = new DBConnector();

    private final PatientService patientService;
    private final SequenceNumberService sequenceNumberService;
//
//    public IvoryHistoricalClinicalDataSindano(PatientService patientService) {
//        this.patientService = patientService;
//    }
//    public static void main(String[] args) {
//        IvoryHistoricalClinicalDataSindano sindano = new IvoryHistoricalClinicalDataSindano();
//        sindano.processData();
//    }

    public void processData() {

        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients

        Connection conn = null;
        try {
            conn = connector.ConnectToPastDB();
            String fetchPatientData = "SELECT e.pv_Entity_No, e.v_Fname,e.v_Mname, e.v_Lname, e.v_Id_No, e.d_Dob,e.d_Dor, e.v_File_No, e.d_Status_Date FROM hospitaldb.dbo_m_Entity  AS e WHERE e.fv_Entity_Type_No = 'PAT'";
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
            //printMissingPatients(patients, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Start remove duplicates */
//        this.conn = conn;
//        patients.removeIf(da -> patientExists(da));
//        
//        for (PatientData data : patients) {
//            insertMissingPatient(data);
//        }
//        
        /* End remove duplicates */
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

            //insertTriage(patients, conn);
            System.out.println("Done inserting vitals");
            //insertDoctorNotes(patients, conn);
            System.out.println("Done inserting doctor notes");
            //insertHistoricalPatientDiagnosis(patients, conn);
            System.out.println("Done inserting diagnosis");
//            insertPrescriptions(patients, conn);
            System.out.println("Done inserting prescriptions");
            insertHistoricalLabResults(patients, conn);
            System.out.println("Done inserting lab results");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLabResults(List<PatientData> patients, Connection conn) {
        try {
            int countOrderNo = 1;
            //insert precription history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent prescription data
                    String precription = "SELECT DrugName, Duration, TimesPerDay,Date FROM clinicdb.t_prescription WHERE PatientId = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(precription);
                    rs = pst2.executeQuery();

                    while (rs.next()) {
                        try {
                            //create doctor request
//                            if (!isDoctorRequestOrderNumberExists("VST-".concat(d.getPvEntityNo()), conn)) {
                            String docRequest = "INSERT INTO smarthealth.patient_doctor_request (created_by, created_on, last_modified_by, last_modified_on, version, fulfiller_comment, fulfiller_status, item_cost_rate, item_rate, notes, order_date, order_number, request_type, urgency,  patient_id, requested_by_id, visit_id,DeploymentCount) VALUES ('system', NOW(), 'system', NOW(), '0', 'Fulfilled', 'Fulfilled', 0.00, 0.00, 'Carried down past clinic data', '" + rs.getString("Date") + "', '" + "LAB-".concat(d.getPvEntityNo()) + "', 'Pharmacy', 'Medium', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), '1', (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ), '" + countOrderNo + "')";
                            pst = conn.prepareStatement(docRequest);
                            pst.execute();
//                            }
                            //add the prescription
                            Integer duration = 0;
                            String durationUnits = "UnSpecified";
                            try {
                                //manipulate duration
                                String durationConcat = rs.getString("Duration").replace("/", ",");
                                String[] durationList = durationConcat.split(",");
                                duration = Integer.valueOf(durationList[0]);
                                String unit = durationList[1];
                                if (unit.equals("7")) {
                                    durationUnits = "Day(s)";
                                }
                                if (unit.equals("12")) {
                                    durationUnits = "Month(s)";
                                }
                                if (unit.equals("52")) {
                                    durationUnits = "Week(s)";
                                }
                            } catch (Exception e) {
//                                System.out.println("Problem encountered manipulating duration units for order number " + "VST-".concat(d.getPvEntityNo()));
                            }
                            String prescriptionNote = "INSERT INTO smarthealth.patient_prescriptions (as_needed, brand_name,  duration,duration_units, frequency, id) VALUES (b'0', '" + rs.getString("DrugName") + "', '" + duration + "', '" + durationUnits + "', '" + rs.getString("TimesPerDay") + "', (SELECT id FROM smarthealth.patient_doctor_request WHERE DeploymentCount ='" + countOrderNo + "' AND order_number =  '" + "LAB-".concat(d.getPvEntityNo()) + "'));";
                            pst = conn.prepareStatement(prescriptionNote);
                            pst.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //System.out.println("error " + e.getMessage());
                        }
                        countOrderNo++;
                    }
                    // pst.executeBatch();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertPrescriptions(List<PatientData> patients, Connection conn) {
        try {
            int countOrderNo = 1;
            //insert precription history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent prescription data
                    String precription = "SELECT DrugName, Duration, TimesPerDay,Date FROM clinicdb.t_prescription WHERE PatientId = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(precription);
                    rs = pst2.executeQuery();

                    while (rs.next()) {
                        System.out.println("d.getPvEntityNo() " + d.getPvEntityNo());
                        try {
                            //create doctor request
//                            if (!isDoctorRequestOrderNumberExists("VST-".concat(d.getPvEntityNo()), conn)) {
                            String docRequest = "INSERT INTO smarthealth.patient_doctor_request (created_by, created_on, last_modified_by, last_modified_on, version, fulfiller_comment, fulfiller_status, item_cost_rate, item_rate, notes, order_date, order_number, request_type, urgency,  patient_id, requested_by_id, visit_id,DeploymentCount) VALUES ('system', NOW(), 'system', NOW(), '0', 'Fulfilled', 'Fulfilled', 0.00, 0.00, 'Carried down past clinic data', '" + rs.getString("Date") + "', '" + "PRESC-".concat(d.getPvEntityNo()) + "', 'Pharmacy', 'Medium', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), '1', (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ), '" + countOrderNo + "')";
                            pst = conn.prepareStatement(docRequest);
                            pst.execute();
//                            }
                            //add the prescription
                            Integer duration = 0;
                            String durationUnits = "UnSpecified";
                            try {
                                //manipulate duration
                                String durationConcat = rs.getString("Duration").replace("/", ",");
                                String[] durationList = durationConcat.split(",");
                                duration = Integer.valueOf(durationList[0]);
                                String unit = durationList[1];
                                if (unit.equals("7")) {
                                    durationUnits = "Day(s)";
                                }
                                if (unit.equals("12")) {
                                    durationUnits = "Month(s)";
                                }
                                if (unit.equals("52")) {
                                    durationUnits = "Week(s)";
                                }
                            } catch (Exception e) {
//                                System.out.println("Problem encountered manipulating duration units for order number " + "VST-".concat(d.getPvEntityNo()));
                            }
                            String prescriptionNote = "INSERT INTO smarthealth.patient_prescriptions (as_needed, brand_name,  duration,duration_units, frequency, id) VALUES (b'0', '" + rs.getString("DrugName") + "', '" + duration + "', '" + durationUnits + "', '" + rs.getString("TimesPerDay") + "', (SELECT id FROM smarthealth.patient_doctor_request WHERE DeploymentCount ='" + countOrderNo + "' AND order_number = '" + "PRESC-".concat(d.getPvEntityNo()) + "' ));";
                            pst = conn.prepareStatement(prescriptionNote);
                            pst.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("error " + e.getMessage());
                        }
                        countOrderNo++;
                    }
                    // pst.executeBatch();
                }

            }
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
                    String triageHistoryNote = "SELECT d_Checked_Date,d_Checked_Date,v_BP,v_Height,v_Pulse_Rate,v_BP,v_Temperature,v_Weight  FROM hospitaldb.dbo_m_triage WHERE pfv_Entity_No = '" + d.getPvEntityNo() + "'";
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
                    String historicalClinicalNotes = "SELECT pd_Doctor_Date, v_Doctor_Comments,v_Remarks   FROM hospitaldb.dbo_t_doctor WHERE pfv_Patient_No = '" + d.getPvEntityNo() + "'";
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
                    // pst.executeBatch();
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
                    String historicalClinicalNotes = "SELECT pd_Doctor_Date, v_Doctor_Comments,v_Remarks , fv_Diagnosis  FROM hospitaldb.dbo_t_doctor WHERE pfv_Patient_No = '" + d.getPvEntityNo() + "'";
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

    private void insertHistoricalLabResults(List<PatientData> patients, Connection conn) {
        try {
            //insert lab results history
            int deployCount = 0;
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    String labRegisterByDate = "SELECT * FROM clinicdb.t_lab WHERE PatientId = '" + d.getPvEntityNo() + "' GROUP BY Date";
                    pst2 = conn.prepareStatement(labRegisterByDate);
                    rs = pst2.executeQuery();
                    while (rs.next()) {
                        String trnId = sequenceNumberService.next(1L, Sequences.Transactions.name());
                        String labNo = sequenceNumberService.next(1L, Sequences.LabNumber.name());

                        String labRegister = "INSERT INTO smarthealth.lab_register (created_by, created_on, last_modified_by, last_modified_on, version, is_walkin, lab_number,transaction_id, patient_no,payment_mode,request_datetime,  status, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', b'0', '" + labNo + "', '" + trnId + "', '" + d.getCurrentPatientNo() + "', 'Cash', '" + rs.getString("Date") + "', 'Complete', (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
                        pst = conn.prepareStatement(labRegister);
                        pst.execute();
                    }

                    //end of labregister 
                    //begin of labregistertest 
                    String QUERY_LAB_TEST = "SELECT * FROM clinicdb.t_lab WHERE PatientId = '" + d.getPvEntityNo() + "' GROUP BY Date,LabRequest";
                    pst2 = conn.prepareStatement(QUERY_LAB_TEST);
                    rs = pst2.executeQuery();
                    while (rs.next()) {

                        String labRegisterTests = "INSERT INTO smarthealth.lab_register_tests (collection_date_time,entry_date_time,is_panel,paid,result_read,status, lab_register_id, test_name,reference_no, visit_id) VALUES ('" + rs.getString("Date") + "', '" + rs.getString("Date") + "',b'0',b'1',b'1','ResultsEntered', (SELECT id FROM smarthealth.lab_register WHERE request_datetime='" + rs.getString("Date") + "' and visit_id = (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' )), '" + rs.getString("LabRequest") + "', (SELECT lab_number FROM smarthealth.lab_register WHERE request_datetime='" + rs.getString("Date") + "' and visit_id = (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' )), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
                        pst = conn.prepareStatement(labRegisterTests);
                        pst.execute();

                    }

                    //end labregister test
                    String historicalLabResults = "SELECT * FROM clinicdb.t_lab WHERE PatientId = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(historicalLabResults);
                    rs = pst2.executeQuery();
                    while (rs.next()) {

                        String labRegisterResults = "";
                        try {

                            String labRegisterId = "";
                            String labNumber = "";
                            String labRegisterQuery = "(SELECT id,lab_number  FROM smarthealth.lab_register WHERE visit_id =(SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ) and request_datetime = '" + rs.getString("Date") + "' )";
                            PreparedStatement pst3 = conn.prepareStatement(labRegisterQuery);
                            ResultSet rs2 = pst3.executeQuery();
                            if (rs2.next()) {
                                labRegisterId = rs2.getString("id");
                                labNumber = rs2.getString("lab_number");
                            }

                            labRegisterResults = "INSERT INTO smarthealth.lab_register_results (created_by, created_on, last_modified_by, last_modified_on, version,analyte,lab_number,patient_no,result_value,results_date,lab_register_test_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("LabRequest") + "','" + labNumber + "', '" + d.getCurrentPatientNo() + "', '" + rs.getString("LabResult") + "', '" + rs.getString("Date") + "',(SELECT id FROM smarthealth.lab_register_tests WHERE reference_no ='" + labNumber + "' AND test_name = '" + rs.getString("LabRequest") + "' and visit_id = (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' )))";

                            pst = conn.prepareStatement(labRegisterResults);
                            pst.execute();

                        } catch (Exception e) {
                            System.out.println("labregister " + labRegisterResults);
                            e.printStackTrace();
                        }
                        deployCount++;
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

    private boolean isDoctorRequestOrderNumberExists(String doctorRequestNo, Connection connection) throws Exception {
        try {
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient_doctor_request WHERE order_number = '" + doctorRequestNo + "'";
            PreparedStatement pst = connection.prepareStatement(validPatientNo);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating patient request order number", e);
        }
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
                if (count >= 1) {
                    break;
                }
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

    private boolean patientExists(PatientData data) {
        try {
//            int count = 0;
//            for (PatientData d : data) {
//                if (count >= 1) {
//                    break;
//                }
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + data.getCurrentPatientNo() + "'";
            pst = conn.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (!rs.next()) {
//                System.out.println(d.getVFname().concat(" ").concat(d.getVMname()).concat(" ").concat(d.getVLname()) + " " + d.getCurrentPatientNo());
                return false;
//                count++;
            } else {
                return true;
            }
//            }
        } catch (Exception e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception ex) {
                Logger.getLogger(IvoryHistoricalClinicalDataSindano.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }
    }

    private void insertMissingPatient(PatientData data) {
        io.smarthealth.organization.person.patient.data.PatientData patient = new io.smarthealth.organization.person.patient.data.PatientData();
        System.out.println("data.getDDOB() " + data.getDDOB());

//        patient.setDateOfBirth(data.getDDOB().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        patient.setDateOfBirth(Instant.ofEpochMilli(data.getDDOB().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        patient.setAllergyStatus("Unknown");
        patient.setBasicNotes("");
        patient.setBloodType("Unknown");
        patient.setCriticalInformation("");
        patient.setGender(data.getGender());
        patient.setGivenName(data.getVFname());
        patient.setMaritalStatus(MaritalStatus.OTHERS);
        patient.setMiddleName(data.getVMname());
        patient.setStatus(PatientStatus.Active);
        patient.setSurname(data.getVLname());
        patient.setPatientNumber(data.getCurrentPatientNo());
//        patientService.createPatient(patient, null);
    }

}
