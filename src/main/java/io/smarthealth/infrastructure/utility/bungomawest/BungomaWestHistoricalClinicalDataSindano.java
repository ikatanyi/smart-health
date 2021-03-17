/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.bungomawest;

//import io.smarthealth.organization.person.domain.enumeration.Gender;

import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.utility.ivorydata.DBConnector;
import io.smarthealth.infrastructure.utility.ivorydata.PatientData;
import io.smarthealth.infrastructure.utility.ivorydata.VisitData;
import io.smarthealth.organization.person.domain.enumeration.MaritalStatus;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.clinical.PatientVisitData;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simon.waweru
 */
@RequiredArgsConstructor
@Service
public class BungomaWestHistoricalClinicalDataSindano {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    Connection conn = null;
    DBConnector connector = new DBConnector();

    private final PatientService patientService;
    private final SequenceNumberService sequenceNumberService;
    private final VisitService visitService;
    int countOrderNo = 0;

    public void processData() {

        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients
        Connection conn = null;
        try {
            System.out.println("START Patients Fetch ");
            conn = connector.ConnectToPastDB();
            String fetchPatientData = "select firstName, middleName, lastName, patientEmail, patientPhone,patientIDNumber, " +
                    "patientIPOPNumber, patientDateOfirth,datePosted,id,insuranceMemberNumber from tbl_registered_patients";
            pst = conn.prepareStatement(fetchPatientData);
            rs = pst.executeQuery();
            while (rs.next()) {
                PatientData data = new PatientData();
                data.setDDOB(rs.getDate("patientDateOfirth"));
                data.setDDor(rs.getDate("datePosted"));
                data.setCurrentPatientNo(rs.getString("patientIPOPNumber").replaceAll("/", "-"));
                data.setPvEntityNo(rs.getString("id"));
                data.setVFileNo(rs.getString("patientIPOPNumber"));
                data.setVFname(rs.getString("firstName"));
                data.setVLname(rs.getString("lastName"));
                data.setVMname(rs.getString("middleName"));
                data.setCardNumber(rs.getString("insuranceMemberNumber"));
                data.setIdNumber(rs.getString("patientIDNumber"));
                patients.add(data);
            }

            System.out.println("DONE Patients Fetch " + patients.size());


        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (PatientData da : patients) {
                System.out.println("Start insert member number");
                insertMemberNumber(da, conn);
                System.out.println("End insert member number");
               /*
                System.out.println("START fetch patient visit ");
                List<Visit> v = fetchPatientVisitData(da, conn);

                //Get patient visit
                for (Visit vs : v) {
                    //insert vitals
                    System.out.println("START vitals insert for visit "+vs.getVisitNumber());
                    insertVisitVital(vs, conn);
                    System.out.println("DONE Inserting all vitals for visit "+vs.getVisitNumber());
                    //insert prescriptions
                    insertPrescriptions(vs, conn);
                    System.out.println("DOne Inserting all prescriptions for visit "+vs.getVisitNumber());
                    insertDiagnosis(vs, conn);
                    System.out.println("DOne Inserting all diagnosis for visit "+vs.getVisitNumber());
                }*/


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMemberNumber(PatientData da, Connection conn) {
//        System.out.println("");
        try {

            String idNumber = "INSERT INTO smarthealth.patient_identification (a_value, patient_id, a_type) " +
                    "VALUES ('" + da.getIdNumber() + "'," +
                    " (SELECT id FROM smarthealth.patient WHERE patient_number = '" + da.getCurrentPatientNo() + "'), " +
                    "'2')";
            pst = conn.prepareStatement(idNumber);
            pst.execute();

            String memBerNumber = "INSERT INTO smarthealth.patient_identification (a_value, patient_id, a_type) " +
                    "VALUES ('" + da.getCardNumber() + "'," +
                    " (SELECT id FROM smarthealth.patient WHERE patient_number = '" + da.getCurrentPatientNo() + "'), " +
                    "'1')";
            pst = conn.prepareStatement(memBerNumber);
            pst.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDiagnosis(Visit vs, Connection conn) {
        try {
            String fetchDiagnosis = "select diagnosisID, diagnosedReferenceCode, diagnosisICDCode, diagnosisName, diagnosisTimeStamp," +
                    " diagnosisImpression from eval_patients_diagnosis WHERE isDeleted = '0' and " +
                    "diagnosedReferenceCode = '" + vs.getVisitNumber() + "' ";
            pst = conn.prepareStatement(fetchDiagnosis);
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("START insert diagnosis for visit number " + vs.getVisitNumber() + " Patient ID " + vs.getPatient().getId());
                countOrderNo++;
                String diagnosis = "INSERT INTO smarthealth.patient_diagnosis (created_by, created_on, last_modified_by, " +
                        "last_modified_on, version, voided, certainty,date_recorded, code, description, diagnosis_order, notes, " +
                        "patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', b'0', NULL, " +
                        "'" + rs.getDate("diagnosisTimeStamp") + "', '" + rs.getString("diagnosisICDCode") + "', " +
                        "'" + rs.getString("diagnosisName") + "', 'Primary', '" + rs.getString("diagnosisImpression").replace("'", "''") + "', " +
                        "(SELECT id FROM smarthealth.patient WHERE patient_number = '" + vs.getPatient().getPatientNumber() + "'), " +
                        "(SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + vs.getVisitNumber() + "' ))";

                System.out.println("Diagnosis " + diagnosis);
                pst = conn.prepareStatement(diagnosis);
                pst.execute();

                System.out.println("DONE insert prescription for " + vs.getVisitNumber() + " countOrderNo " + countOrderNo);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
        }
    }


    private void insertPrescriptions(Visit vs, Connection conn) {
        try {
            String fetchPrescriptions = "SELECT p.prescriptionDrugDuration, p.prescriptionDrugDosage, p.prescriptionDrugTime," +
                    "p.prescriptionDrugAdministrationRoute,p.prescriptionDrugPrice, i.genericName, i.brandName,p.prescriptionTimeStamp\n" +
                    "FROM eval_patients_prescriptions as p\n" +
                    "inner join invms_stkitem as i on i.id = p.prescriptionDrugID WHERE p.prescriptionReferenceCode = '" + vs.getVisitNumber() + "' ";
            pst = conn.prepareStatement(fetchPrescriptions);
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("START insert prescription for visit number " + vs.getVisitNumber() + " Patient ID " + vs.getPatient().getId());
                countOrderNo++;
                String docRequest = "INSERT INTO smarthealth.patient_doctor_request (created_by, created_on, last_modified_by," +
                        " last_modified_on, version, fulfiller_comment, fulfiller_status, item_cost_rate, item_rate, " +
                        "notes, order_date, order_number, request_type, urgency,  patient_id, requested_by_id, visit_id,DeploymentCount) " +
                        "VALUES ('system', NOW(), 'system', NOW(), '0', 'Fulfilled', 'Fulfilled', 0.00, 0.00, 'Carried down past clinic data', " +
                        "'" + rs.getDate("prescriptionTimeStamp") + "', '" + countOrderNo + "', 'Pharmacy', 'Medium'," +
                        " '" + vs.getPatient().getId() + "', '1', " +
                        "(SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + vs.getVisitNumber() + "' ), " +
                        "'" + countOrderNo + "')";
                System.out.println("Doc Request " + docRequest);
                pst = conn.prepareStatement(docRequest);
                pst.execute();

                String prescriptionNote = "INSERT INTO smarthealth.patient_prescriptions (as_needed, brand_name,  duration," +
                        "duration_units, frequency, id) VALUES (b'0', '" + rs.getString("brandName") + "', '" + rs.getInt("prescriptionDrugDuration") + "'," +
                        " '" + rs.getString("prescriptionDrugDosage") + "', '" + rs.getString("prescriptionDrugTime") + "', " +
                        "(SELECT id FROM smarthealth.patient_doctor_request WHERE " +
                        "DeploymentCount ='" + countOrderNo + "' AND order_number = '" + countOrderNo + "' ));";

                System.out.println("prescriptionNote " + prescriptionNote);
                pst2 = conn.prepareStatement(prescriptionNote);
                pst2.execute();


                System.out.println("DONE insert prescription for " + vs.getVisitNumber() + " countOrderNo " + countOrderNo);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
        }
    }

    //fetch visits
    private List<Visit> fetchPatientVisitData(PatientData pd, Connection conn) {
        Optional<Patient> p = patientService.findPatientByPatientNumber(pd.getCurrentPatientNo().trim());
        if (!p.isPresent()) {
            System.out.println("Patient " + pd.getCurrentPatientNo() + " not present ");

            return new ArrayList<>();
        }


        Patient patient = p.get();
        System.out.println("Patient Number " + patient.getPatientNumber());

        List<Visit> v = new ArrayList<>();
        String visitData = "SELECT id, VisitId, date_Created,patientID,date_created, closed_date FROM  visit_db WHERE PatientID = '" + pd.getPvEntityNo() + "'";
        try {
            pst2 = conn.prepareStatement(visitData);
            rs = pst2.executeQuery();
            while (rs.next()) {
                //check if visit exists
                if (visitService.findVisit(rs.getString("VisitId")).isPresent()) {
                    System.err.println("Visit Identified by " + rs.getString("VisitId") + " exists ");
                    continue;
                }
                //Create new patient visit
                Visit visit = new Visit();
                visit.setPatient(patient);
                visit.setVisitNumber(rs.getString("VisitId"));
                visit.setStartDatetime(rs.getTimestamp("date_created").toLocalDateTime());
                visit.setStopDatetime(LocalDateTime.now());//rs.getTimestamp("closed_date").toLocalDateTime()
                visit.setStatus(VisitEnum.Status.CheckOut);
                visit.setIsActiveOnConsultation(Boolean.FALSE);
                Visit createdVisit = visitService.createAVisit(visit);
                v.add(createdVisit);
                System.out.println("DONE CREATING VISIT " + createdVisit.getVisitNumber() + " FOR " + patient.getPatientNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                throw e;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return v;
    }

    private void insertVisitVital(Visit visit, Connection conn) {
        try {
            String fetchVitals = "SELECT dateUpdated, patientUID, patientWeight, patientHeight, patientTemperature, patientBPSystolic," +
                    " patientBPDiastolic, patientPulsePerMinute, patientOxygenSaturation, patientRespiratoryRatePerMinute, nursesNotes FROM eval_nurses_queue WHERE patientQueueReferenceCode = '" + visit.getVisitNumber() + "' ";
            pst = conn.prepareStatement(fetchVitals);
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("START VITAL INSERT " + visit.getVisitNumber() + " Patient ID " + visit.getPatient().getId());
                String triageNote = "INSERT INTO smarthealth.patient_vitals_record (created_by, created_on, last_modified_by, " +
                        "last_modified_on, version, date_recorded, voided, bmi, category, comments, diastolic, height, pulse, " +
                        "spo2, systolic, temp, weight, patient_id, visit_id) " +
                        "VALUES ('system', NOW(), 'system', NOW(), '0', NOW(), " +
                        "b'0', NULL, NULL, 'VISIT B/U', '" + rs.getDouble("patientBPDiastolic") + "',  '" + rs.getString("patientHeight") + "', " +
                        "'" + rs.getString("patientRespiratoryRatePerMinute") + "',NULL, '" + rs.getString("patientBPSystolic") + "', '"
                        + rs.getString("patientTemperature") + "', '" + rs.getString("patientWeight") + "', " +
                        "'" + visit.getPatient().getId() + "', " +
                        "(SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + visit.getVisitNumber() + "' ))";
                System.out.println("Triage Note " + triageNote);
                pst2 = conn.prepareStatement(triageNote);
                pst2.execute();

                System.out.println("DONE VITAL INSERT " + visit.getVisitNumber());

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
                Logger.getLogger(BungomaWestHistoricalClinicalDataSindano.class.getName()).log(Level.SEVERE, null, ex);
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
