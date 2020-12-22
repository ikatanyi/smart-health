/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.clinical.moh.data.Register;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Simon.waweru
 */
public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit>, ResultsRepository {

    Page<Visit> findByPatientOrderByStartDatetimeDesc(final Patient patient, Pageable page);

    Page<Visit> findByPatientAndVisitNumber(final Patient patient, final String visitNumber, Pageable page);

    Page<Visit> findByServicePointAndStatusNot(final ServicePoint servicePoint, final VisitEnum.Status status, Pageable page);

    Optional<Visit> findByVisitNumber(String visitNumber);

    Page<Visit> findByStatus(final VisitEnum.Status status, Pageable pageable);

    Optional<Visit> findByVisitNumberAndStatus(final String visitNumber, final VisitEnum.Status status);

    Optional<Visit> findByVisitNumberAndStatusNot(final String visitNumber, final VisitEnum.Status status);

    //Page<Visit> findByStatus(final String status, final Pageable pageable);
    Optional<Visit> findByPatientAndStatus(Patient patient, VisitEnum.Status status);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Visit c WHERE c.status=:currentStatus AND  c.patient.patientNumber = :patient")
    Boolean visitExists(@Param("currentStatus") final String status, @Param("patient") final String patient);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM Visit c WHERE (c.status='CheckIn' OR c.status = 'Admitted') and c.patient=:patient")
    Boolean isPatientVisitActive(@Param("patient") Patient patient);

    @Query(value = "SELECT max(id) FROM Visit")
    public Integer maxVisitId();

    @Query(value = "SELECT DISTINCT(c.healthProvider) FROM Visit c WHERE (c.status='CheckIn' OR c.status = 'Admitted')")
    List<Employee> practionersByActiveVisits();

    @Query(value = "SELECT COUNT(c) FROM Visit c WHERE (c.status='CheckIn' OR c.status = 'Admitted')")
    Long countWhereVisitsAreActive();

    @Query(value = "SELECT v FROM Visit v WHERE  TIMESTAMPDIFF(hour, start_datetime,now())  >=:hours AND v.status = 'CheckIn'")
    List<Visit> visitsPast24hours(@Param("hours") int hours);

    @Query(value = "SELECT v FROM Visit v WHERE v.patient=:patient AND  v.visitNumber <> :visitNumber ORDER BY v.id DESC")
    Page<Visit> lastVisit(@Param("patient") Patient patient, @Param("visitNumber") String visitNumber, Pageable pageable);

    @Query(value = "SELECT v FROM Visit v WHERE v.patient=:patient  ORDER BY v.id DESC")
    Page<Visit> lastVisitWithoutCurrentActiveVisit(@Param("patient") Patient patient, Pageable pageable);

    @Query(value = "SELECT v FROM Visit v WHERE DATE(v.startDatetime)=:date GROUP BY v.patient.patientNumber, DATE(v.startDatetime)")
    List<Visit> visitAttendance(@Param("date") Date date);

    @Query(value = "SELECT v.patient.patientNumber AS patientNumber, v.patient.fullName AS fullName, v.patient.age AS age, v.patient.gender AS gender, p.diagnosis.description AS diagnosis,v.patient.residence AS residence, v.startDatetime  AS date, v.startDatetime AS seen, v.patient.createdBy AS createdBy  from Visit v LEFT JOIN PatientDiagnosis p ON v.id = p.visit.id WHERE v.startDatetime BETWEEN :fromDate AND :toDate GROUP BY v.patient.patientNumber,v.visitNumber")
    List<Register> patientRegister(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    
@Query(value = "SELECT r.order_date as date, r.patient_id, CONCAT_WS(' ',p.given_name,p.middle_name,p.surname) AS patientName,  'Laboratory' AS ServicePoint, r.visit_id AS VisitId, r.created_on AS Start, l.created_on AS Acknowledged, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),0.0) AS total, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),'AWAITING')  AS TAT" +
" FROM patient_doctor_request r " +
" LEFT JOIN lab_register l ON r.visit_id=l.visit_id" +
" LEFT JOIN person p ON r.patient_id=p.id" +
" WHERE r.visit_id= :visitId AND request_type='Laboratory'" +
" GROUP BY ServicePoint" +
" UNION" +
" SELECT r.order_date as date, r.patient_id, CONCAT_WS(' ',p.given_name,p.middle_name,p.surname) AS patientName, 'Procedure' AS ServicePoint,r.visit_id AS VisitId, r.created_on AS Start, l.created_on AS Acknowledged,IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),0.0) AS total, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),'AWAITING')  AS TAT" +
" FROM patient_doctor_request r " +
" LEFT JOIN patient_procedure_register l ON r.visit_id=l.visit_id" +
" LEFT JOIN person p ON r.patient_id=p.id" +
" WHERE r.visit_id= :visitId AND request_type='Procedure'" +
" GROUP BY ServicePoint" +
" UNION " +
" SELECT r.order_date as date, r.patient_id,CONCAT_WS(' ',p.given_name,p.middle_name,p.surname) AS patientName,'Pharmacy' AS ServicePoint,r.visit_id AS VisitId, r.created_on AS Start, l.created_on AS Acknowledged,IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),0.0) AS total, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),'AWAITING')  AS TAT" +
" FROM patient_doctor_request r " +
" LEFT JOIN pharmacy_dispensed_drugs l ON r.visit_id=l.visit_id" +
" LEFT JOIN person p ON r.patient_id=p.id" +
" WHERE r.visit_id= :visitId AND request_type='Pharmacy'" +
" GROUP BY ServicePoint" +
" UNION " +
" SELECT r.order_date as date, r.patient_id,CONCAT_WS(' ',p.given_name,p.middle_name,p.surname) AS patientName,'Radiology' AS ServicePoint,r.visit_id AS VisitId, r.created_on AS Start, l.created_on AS Acknowledged, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),0.0) AS total, IFNULL(TIMESTAMPDIFF(MINUTE,r.created_on,l.created_on),'AWAITING')  AS TAT" +
" FROM patient_doctor_request r " +
" LEFT JOIN patient_scan_register l ON r.visit_id=l.visit_id" +
" LEFT JOIN person p ON r.patient_id=p.id" +
" WHERE r.visit_id= :visitId AND request_type='Radiology'" +
" GROUP BY ServicePoint,r.visit_id" +
" UNION " +
" SELECT n.date_recorded as date, v.patient_id,CONCAT_WS(' ',p.given_name,p.middle_name,p.surname) AS patientName, 'Consultation' AS ServicePoint,v.id AS VisitId,  v.created_on AS Start, n.created_on AS Acknowledged, IFNULL(TIMESTAMPDIFF(MINUTE,v.created_on,n.created_on),0.0) AS total, IFNULL(TIMESTAMPDIFF(MINUTE,v.created_on,n.created_on),'AWAITING') AS TAT FROM patient_visit v " +
" LEFT JOIN patient_clinical_notes n ON v.id=n.visit_id " +
" LEFT JOIN person p ON n.patient_id=p.id" +
" WHERE n.visit_id= :visitId" +
" GROUP BY ServicePoint", nativeQuery=true)
    List<TatInterface> patientTatStatement(Long visitId);

}
