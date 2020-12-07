/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.accounting.billing.data.nue.BillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.moh.data.Register;
import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.TatInterface;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.clinical.visit.domain.specification.ReportVisitSpecification;
import io.smarthealth.clinical.visit.domain.specification.VisitSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.security.domain.User;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final ServicePointService servicePointService;
    private final PatientRepository patientRepository;
    private final EmployeeService employeeService;
    private final BillingService billingService;
    private final ConfigService configService;
 
    public Page<Visit> fetchVisitByPatientNumber(String patientNumber, final Pageable pageable) {
        Patient patient = findPatientOrThrow(patientNumber);
        Page<Visit> visits = visitRepository.findByPatientOrderByStartDatetimeDesc(patient, pageable);
        return visits;
    }

    public Page<Visit> fetchVisitByPatientNumberAndVisitNumber(String patientNumber, String visitNumber, final Pageable pageable) {
        Patient patient = findPatientOrThrow(patientNumber);
        Page<Visit> visits = visitRepository.findByPatientAndVisitNumber(patient, visitNumber, pageable);
        return visits;
    }

    public Page<Visit> fetchAllVisits(final String visitNumber, final String staffNumber, final ServicePointType servicePointType, final String patientNumber, final String patientName, boolean runningStatus, DateRange range, final Boolean isActiveOnConsultation, final String username, final boolean orderByTriageCategory, final String queryTerm, final Boolean billPaymentValidation, final Pageable pageable) {
        Employee employee = null;
        ServicePoint servicePoint = null;
        Patient patient = null;
        User user = null;
        if (staffNumber != null) {
            employee = employeeService.fetchEmployeeByNumberOrThrow(staffNumber);
        }
        if (servicePointType != null) {
//            servicePoint = servicePointService.getServicePointByType(ServicePointType.valueOf(servicePointType));
            servicePoint = servicePointService.getServicePointByType(servicePointType);
        }
        if (patientNumber != null) {
            patient = findPatientOrThrow(patientNumber);
        }

        if (username != null) {
//            user = userService.findUserByUsernameOrEmail(username).orElseThrow(() -> APIException.notFound("User {0} not found ", username));
            Optional<Employee> presentEmployee = employeeService.findEmployeeByUsername(username);
            if (presentEmployee.isPresent()) {
                employee = presentEmployee.get();
            } else {
                employee = null;
            }
        }

        if (billPaymentValidation != null) {
            if (billPaymentValidation && servicePoint == null) {
                throw APIException.badRequest("Please specify service point to validate service point bill payment", "");
            }
        }

        Specification<Visit> visitSpecs = VisitSpecification.createSpecification(visitNumber, employee, servicePoint, patient, patientName, runningStatus, range, isActiveOnConsultation, orderByTriageCategory, queryTerm);
        Page<Visit> visits = visitRepository.findAll(visitSpecs, pageable);
//        List<Visit> visitList = visits.getContent();//new ArrayList<>(Arrays.asList());
//        List visitList = Arrays.asList(visits.getContent());

        List<Visit> visitData = new ArrayList<>(visits.getContent());
//        visitData = visits.getContent();
        if (billPaymentValidation) {
            for (Visit v : visits.getContent()) {
//fetchPatientBillItems by visit number
                List<BillItem> items = billingService.getAllBillDetails(v.getVisitNumber(), false);
                //items.forEach(System.out::println);

                for (BillItem bi : items) {
                    //if item equals "DoctorFee" && item.amount > 0
                    if (bi.getItemCategory().equals(ItemCategory.DoctorFee) && !bi.getPaid() && servicePointType.equals(ServicePointType.Consultation)) {
                        // visitData.add(v);
                        visitData.remove(v);
//                        visitList.remove(v);
                        System.out.println("tally count");
                        break;
                    }
                }
            }
        }

        Page<Visit> newVisitsPage = new PageImpl<>(visitData, pageable, visitData.size());

        return newVisitsPage;
    }

    //@Transactional
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Visit createAVisit(final Visit visit) {
//        try {
        return visitRepository.save(visit);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw APIException.internalError("There was an error creating visit", e.getMessage());
//        }
    }

    public String updateVisit(final String visitNumber, final VisitData visitDTO) {
        findVisitEntityOrThrow(visitNumber);
        //validate and fetch patient
        Patient patient = findPatientOrThrow(visitDTO.getPatientNumber());

        Visit visitEntity = VisitData.map(visitDTO);
        visitEntity.setPatient(patient);
        visitRepository.save(visitEntity);
        return visitDTO.getVisitNumber();
    }

    public boolean isPatientVisitActive(Patient patient) {
        return visitRepository.isPatientVisitActive(patient);
    }

    public Patient findPatientOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }

    public Page<Visit> findVisitByStatus(final VisitEnum.Status status, Pageable pageable) {
        return visitRepository.findByStatus(status, pageable);
    }

    public Page<Visit> findVisitByServicePoint(final ServicePoint servicePoint, Pageable pageable) {
        return visitRepository.findByServicePointAndStatusNot(servicePoint, VisitEnum.Status.CheckOut, pageable);
    }

    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    public Optional<Visit> findVisitById(Long id) {
        return this.visitRepository.findById(id);
    }

    public Optional<Visit> findVisit(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber);
    }

    public Optional<Visit> fetchVisitByPatientAndStatus(final Patient patient, final VisitEnum.Status status) {
        return this.visitRepository.findByPatientAndStatus(patient, status);
    }

    public Page<Visit> lastVisit(final Patient patient, final String currentVisitNumber) {
        if (currentVisitNumber != null) {
            return this.visitRepository.lastVisit(patient, currentVisitNumber, PageRequest.of(0, 1));
        } else {
            //find current visit
            Optional<Visit> visit = visitRepository.findByPatientAndStatus(patient, VisitEnum.Status.CheckIn);
            if (visit.isPresent()) {
                return this.visitRepository.lastVisit(patient, visit.get().getVisitNumber(), PageRequest.of(0, 1));
            }
            return this.visitRepository.lastVisitWithoutCurrentActiveVisit(patient, PageRequest.of(0, 1));
        }
    }

    public VisitData convertVisitEntityToData(Visit visit) {
        return VisitData.map(visit);
    }

    public List<Employee> practionersByActiveVisits() {
        return visitRepository.practionersByActiveVisits();
    }

    public List<Visit> fetchAllVisitsSurpassed24hrs() {
        Optional<GlobalConfiguration> conf = configService.findByName("AutomaticVisitCheckOutTime");
        int hours = 24;
        if (conf.isPresent()) {
            hours = Integer.valueOf(conf.get().getValue());
        }
        return visitRepository.visitsPast24hours(hours);
    }

    public List<Visit> fetchVisitAttendance(Date date) {
        return visitRepository.visitAttendance(date);
    }

    public List<Register> getPatientRegister(DateRange range) {
        return visitRepository.patientRegister(range.getStartDateTime(), range.getEndDateTime());
    }

    public Page<Visit> fetchVisitsGroupByVisitNumber(final String visitNumber, final String staffNumber, final String servicePointType, final String patientNumber, final String patientName, Boolean runningStatus, DateRange range, final Pageable pageable) {
// Visit visit = null;
        Employee employee = null;
        ServicePoint servicePoint = null;
        Patient patient = null;
// if (visitNumber != null) {
// // visit = this.findVisitEntityOrThrow(visitNumber);
// }
        if (staffNumber != null) {
            employee = employeeService.fetchEmployeeByNumberOrThrow(staffNumber);
        }
        if (servicePointType != null) {
            servicePoint = servicePointService.getServicePointByType(ServicePointType.valueOf(servicePointType));
        }
        if (patientNumber != null) {
            patient = findPatientOrThrow(patientNumber);
        }

// System.out.println(" LocalDate.now().atStartOfDay() " + LocalDate.now().atStartOfDay());
        Specification<Visit> visitSpecs = ReportVisitSpecification.createSpecification(visitNumber, employee, servicePoint, patient, patientName, runningStatus, range);
        Page<Visit> visits = visitRepository.findAll(visitSpecs, pageable);
        return visits;
    }

    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }

    public List<DocResults> getPatientResultsAlerts(String visitNumber, String patientNumber, DocResults.Type type, DateRange range, String patientName, String username, Boolean showResultsRead) {
        Employee employee = null;
        if (username != null) {
            Optional<Employee> em = employeeService.findEmployeeByUsername(username);
            if (em.isPresent()) {
                employee = em.get();
            }
        }
        return visitRepository.getPatientResults(visitNumber, patientNumber, type, range, patientName, employee, showResultsRead);
    }

    public List<TatInterface> getPatientTat(Long visitId) {
        return visitRepository.patientTatStatement(visitId);
    }

      public String patientActiveVisit(final Long patientId){
        System.out.println("To search "+patientId);
        return  visitRepository.patientActiveVisit(patientId);
    }

    public Page<Visit> getSimpleVisits(String visitNumber, String patientNumber, DateRange dateRange, Pageable page) {
        Specification<Visit> spec = VisitSpecification.createSpecification(visitNumber, patientNumber, dateRange);
        return visitRepository.findAll(spec, page);
    }
}
