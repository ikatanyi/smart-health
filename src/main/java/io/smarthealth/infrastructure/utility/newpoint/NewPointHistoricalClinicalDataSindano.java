/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.newpoint;

import io.smarthealth.infrastructure.utility.ivorydata.PatientData;
import io.smarthealth.infrastructure.utility.vimakdata.DBConnector;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@RequiredArgsConstructor
@Service
public class NewPointHistoricalClinicalDataSindano {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    Connection conn = null;
    DBConnector connector = new DBConnector();
    String MS_SQL = "jdbc:sqlserver://LP-TECH-4Z0Y\\SQLEXPRESS:1433;"
            + "database=NewPaint_LimSoft_DB;"
            + "user=sa;"
            + "password=Admin@12345;"
            + "encrypt=true;"
            + "trustServerCertificate=false;"
            + "loginTimeout=60;";

    private final PatientService patientService;
    private final SequenceNumberService sequenceNumberService;

    public void processData() {

        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients

        Connection conn = null;
        Connection msconn = null;
        try {
            msconn = connector.msConnection();
            conn = connector.ConnectToPastDB();
            String fetchPatientData = "SELECT p.RegID, p.FileNo,FirstName,LastName,ParentsName FROM limpatients AS p ";
            pst = conn.prepareStatement(fetchPatientData);
            rs = pst.executeQuery();
            while (rs.next()) {
                PatientData data = new PatientData();
                data.setPvEntityNo(rs.getString("RegID"));
                data.setVFileNo(rs.getString("FileNo"));
                data.setVFname(rs.getString("FirstName"));
                data.setVLname(rs.getString("LastName"));
                data.setVMname(rs.getString("ParentsName"));

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

//            insertTriage(patients, conn, msconn);
//            System.out.println("Done inserting vitals");
            insertDoctorNotes(patients, conn, msconn);
//            System.out.println("Done inserting doctor notes");
            //insertHistoricalPatientDiagnosis(patients, conn);
//            System.out.println("Done inserting diagnosis");
            insertPrescriptions(patients, conn, msconn);
//            System.out.println("Done inserting prescriptions");
//            //insertHistoricalLabResults(patients, conn);
//            System.out.println("Done inserting lab results");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertTriage(List<PatientData> patients, Connection conn, Connection msconn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent triage data
                    String triageHistoryNote = "SELECT TransDate,TransDate,BloodPressure,Height,Pulse,Temperature,Weight  FROM NewPaint_LimSoft_DB.dbo.limexamination WHERE RegID = '" + d.getPvEntityNo() + "'";
                    pst2 = conn.prepareStatement(triageHistoryNote);
                    rs = pst2.executeQuery();
                    while (rs.next()) {

                        try {
                            String dia = null;
                            String sys = null;

                            try {
                                String names = rs.getString("BloodPressure").replace("/", ",");
                                names = names.replaceAll("mmsg", "").trim();
                                String[] bpList = names.split(",");
                                dia = bpList[1];
                                sys = bpList[0];
                            } catch (Exception e) {
                                System.out.println("Systolic Diastolic manipulation error");
                            }

                            String triageNote = "INSERT INTO smarthealth.patient_vitals_record (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, bmi, category, comments, diastolic, height, pulse, spo2, systolic, temp, weight, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("TransDate") + "', b'0', NULL, NULL, 'VISIT B/U', '" + dia + "',  '" + rs.getString("Height") + "', '" + rs.getString("Pulse") + "',NULL, '" + sys + "', '" + rs.getString("Temperature") + "', '" + rs.getString("Weight") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
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

    private String harmonizePatientNo(PatientData data) {
        String newPatientNo = "";
        newPatientNo = newPatientNo.concat("NMC-").concat(data.getPvEntityNo());
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

    private void insertDoctorNotes(List<PatientData> patients, Connection conn, Connection msconn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent triage data
                    String historicalClinicalNotes = "SELECT TransDate, CaseHistory,CaseHistory FROM dbo.LimPatientCaseHistory WHERE RegID = '" + d.getPvEntityNo() + "'";
                    pst2 = msconn.prepareStatement(historicalClinicalNotes);
                    rs = pst2.executeQuery();
                    while (rs.next()) {
                        String clinicalNotes = "";
                        try {
                            clinicalNotes = "INSERT INTO smarthealth.patient_clinical_notes (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, chief_complaint, examination_notes, health_provider_id, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("TransDate") + "', b'0', '" + rs.getString("CaseHistory").replaceAll("'", "''") + "', '" + rs.getString("CaseHistory").replace("'", "''") + "', NULL, (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ))";
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

    private void insertPrescriptions(List<PatientData> patients, Connection conn, Connection msconn) {
        try {
            int countOrderNo = 1;
            //insert precription history
            for (PatientData d : patients) {
                if (patientAvailable(d.getCurrentPatientNo(), conn)) {
                    //find equivalent prescription data
                    String precription = "select pm.PrescriptionID, pm.RegID as PatientNo,pm.PreDate, ls.DrugCategory, ls.GenericName,ls.ItemName, pd.Dosage,pd.Frequency, \n"
                            + "pd.Instructions,pd.NoOfDays,pd.Qty,pd.rate  from LimPrescriptionMaster as pm\n"
                            + "inner join LimPrescriptionDetails as pd ON pd.PrescriptionID = pm.PrescriptionID\n"
                            + "inner join LimStocks as ls on ls.ItemID = pd.ItemID WHERE RegID = '" + d.getPvEntityNo() + "'";
                    pst2 = msconn.prepareStatement(precription);
                    rs = pst2.executeQuery();

                    while (rs.next()) {
                        System.out.println("PatientNo " + d.getPvEntityNo());
                        try {
                            //create doctor request
//                            if (!isDoctorRequestOrderNumberExists("VST-".concat(d.getPvEntityNo()), conn)) {
                            String docRequest = "INSERT INTO smarthealth.patient_doctor_request (created_by, created_on, last_modified_by, last_modified_on, version, fulfiller_comment, fulfiller_status, item_cost_rate, item_rate, notes, order_date, order_number, request_type, urgency,  patient_id, requested_by_id, visit_id,DeploymentCount) VALUES ('system', NOW(), 'system', NOW(), '0', 'Fulfilled', 'Fulfilled', 0.00, 0.00, 'Carried down past clinic data', '" + rs.getString("PreDate") + "', '" + "PRESC-".concat(d.getPvEntityNo()) + "', 'Pharmacy', 'Medium', (SELECT id FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'), '1', (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-".concat(d.getPvEntityNo()) + "' ), '" + countOrderNo + "')";
                            pst = conn.prepareStatement(docRequest);
                            pst.execute();
//                            }
                            //add the prescription
                            Integer duration = 0;
                            String durationUnits = "UnSpecified";
                            try {
                                //manipulate duration
                                String durationConcat = rs.getString("NoOfDays").replace("/", ",");
//                                String[] durationList = durationConcat.split(",");
                                duration = Integer.valueOf(durationConcat);
                                String unit = durationConcat;
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
                            String prescriptionNote = "INSERT INTO smarthealth.patient_prescriptions (as_needed, brand_name,  duration,duration_units, frequency, id) VALUES (b'0', '" + rs.getString("ItemName") + "', '" + duration + "', '" + durationUnits + "', '" + rs.getString("Frequency") + "', (SELECT id FROM smarthealth.patient_doctor_request WHERE DeploymentCount ='" + countOrderNo + "' AND order_number = '" + "PRESC-".concat(d.getPvEntityNo()) + "' ));";
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

}
