package io.smarthealth.clinical.admission.service;

import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.data.CareTeamData;
import io.smarthealth.clinical.admission.data.OPAdmissionData;
import io.smarthealth.clinical.admission.domain.*;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRequestRepository;
import io.smarthealth.clinical.admission.domain.specification.AdmissionRequestSpecification;
import io.smarthealth.clinical.admission.domain.specification.AdmissionSpecification;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.visit.data.PaymentDetailsData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import lombok.RequiredArgsConstructor;
import org.apache.tools.ant.taskdefs.War;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final SequenceNumberService sequenceNumberService;
    private final WardService wardService;
    private final BedService bedService;
    private final RoomService roomService;
    private final PatientService patientService;
    private final EmployeeService employeeService;
    private final PaymentDetailsService paymentDetailsService;
    private final BillingService billingService;
    private final SchemeService schemeService;
    private final VisitService visitService;
    private final UserService userService;
    private final AdmissionRequestRepository admissionRequestRepository;

    @Transactional
    public Admission createAdmission(AdmissionData d) {
        //validate if patient has an active 

        Ward w = wardService.getWard(d.getWardId());
        Bed b = bedService.getBed(d.getBedId());
        BedType bt = bedService.getBedType(d.getBedTypeId());
        Patient p = patientService.findPatientOrThrow(d.getPatientNumber());
        Optional<Visit> visit = visitService.fetchVisitByPatientAndStatus(p, Status.CheckIn);
        if (visit.isPresent()) {
            throw APIException.badRequest("Patient has an already existing visit.", "");
        }

        if (findAdmissionByPatientAndStatus(d.getPatientNumber()).isPresent()) {
            throw APIException.conflict("Patient {0} already Admitted.", d.getPatientNumber());
        }

        String admissionNo = sequenceNumberService.next(1l, Sequences.Admission.name());

        Admission a = AdmissionData.map(d);
        a.setAdmissionNo(admissionNo);
        a.setWard(w);
        a.setBed(b);
        a.setBedType(bt);
        a.setPatient(p);
        if (d.getRoomId() != null) {
            Room room = roomService.getRoom(d.getRoomId());
            a.setRoom(room);
        }

        //visit data
        a.setVisitNumber(admissionNo);
        a.setStartDatetime(d.getAdmissionDate());
        a.setVisitType(VisitEnum.VisitType.Inpatient);
        a.setComments(d.getNarration());
        a.setScheduled(Boolean.FALSE);
        a.setIsActiveOnConsultation(Boolean.FALSE);
        a.setServiceType(VisitEnum.ServiceType.Admission);
        a.setStatus(VisitEnum.Status.Admitted);
        a.setPaymentMethod(d.getPaymentMethod());

        List<CareTeam> ctList = d.getCareTeam().stream().map(c
                        -> {
                    CareTeam ct = CareTeamData.map(c);
                    ct.setAdmission(a);
                    ct.setMedic(employeeService.findEmployeeById(c.getMedicId()));
                    ct.setPatient(p);
                    return ct;
                }
        ).collect(Collectors.toList());

        a.setCareTeam(ctList);

        b.setStatus(Bed.Status.Occupied);

        bedService.updateBed(b);

        List<EmergencyContact> EcList = d.getEmergencyContactData().stream().map(c -> {
            EmergencyContact contact = c.map();
            contact.setAdmission(a);
            return contact;
        }).collect(Collectors.toList());
        a.setEmergencyContacts(EcList);
        Optional<CareTeamData> hp = d.getCareTeam().stream().filter(x -> x.getRole() == CareTeamRole.Admitting)
                .findFirst();
        if (hp.isPresent()) {
            Employee admittingDoctor = employeeService.findEmployeeById(hp.get().getMedicId());
            a.setHealthProvider(admittingDoctor);
        }

        Admission savedAdmissions = admissionRepository.save(a);
        //payment data
        Scheme scheme = null;
        if (d.getPaymentMethod().equals(PaymentMethod.Insurance)) {
            scheme = schemeService.fetchSchemeById(d.getPaymentDetailsData().getSchemeId());
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
            PaymentDetails pd = PaymentDetailsData.map(d.getPaymentDetailsData());

            pd.setScheme(scheme);
            pd.setPayer(scheme.getPayer());
            pd.setVisit(savedAdmissions);
            pd.setPatient(p);
            if (config.isPresent()) {
                pd.setCoPayCalcMethod(config.get().getCoPayType());
                pd.setCoPayValue(config.get().getCoPayValue());
            }
            pd.setLimitEnabled(Boolean.FALSE);
            pd.setLimitReached(Boolean.FALSE);
            pd.setLimitAmount(999999);

            paymentDetailsService.createPaymentDetails(pd);
            //create bill for copay
            if (config.isPresent() && config.get().getCoPayValue() > 0) {
                billingService.createCopay(new CopayData(admissionNo, d.getPaymentDetailsData().getSchemeId()));
            }
            System.out.println("End of if");
        }
        //update patient inpatient file number 
        p.setInpatientNumber(d.getInpatientNumber());
        patientService.savePatient(p);

        billingService.createFee(admissionNo, ItemCategory.Admission, 1);

        //OP to IP handling
        if(d.getOpVisitNumber()!=null || d.getAdmissionRequestId()!=null ){
            //update request to fulfilled
            AdmissionRequest admissionRequest =
                    admissionRequestRepository.findById(d.getAdmissionRequestId()).orElseThrow(()-> APIException.notFound("Admission request identified by {0} not found ", d.getAdmissionRequestId()));

            User user =
                    userService.findUserByUsernameOrEmail(SecurityUtils.getCurrentUserLogin().orElse("")).orElse(null);
            admissionRequest.setFulfillerStatus(FullFillerStatusType.Fulfilled);
            admissionRequest.setFulfillerComment(FullFillerStatusType.Fulfilled.name());
            admissionRequest.setFullfilledBy(user);
            admissionRequest.setAdmissionDateTime(LocalDateTime.now());
            admissionRequestRepository.save(admissionRequest);

            //Update OP visit to Admitted
            Visit opVisit = visitService.findVisitEntityOrThrow(d.getOpVisitNumber());
            opVisit.setComments(d.getAdmittingReason());
            opVisit.setStatus(Status.Admitted);
            visitService.createAVisit(opVisit);
        }

        return savedAdmissions;

    }

    public AdmissionRequest createAdmissionRequest(final OPAdmissionData data) {
        Ward ward = wardService.getWard(data.getWardId());
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
        User user =
                userService.findUserByUsernameOrEmail(data.getAdmittingDoctorusername()).orElseThrow(()->APIException.notFound("User identified by {0} not found ", data.getAdmittingDoctorusername()));
        AdmissionRequest request = new AdmissionRequest();
        request.setRequestDate(data.getAdmissionDate());
        request.setRequestedBy(user);
        request.setFulfillerComment(FullFillerStatusType.Unfulfilled.name());
        request.setFulfillerStatus(FullFillerStatusType.Unfulfilled);
        request.setNotes(data.getAdmissionReason());
        request.setOpVisit(visit);
        request.setOrderNumber(visit.getVisitNumber());
        request.setWard(ward);
        request.setPatient(visit.getPatient());
        request.setUrgency(data.getUrgency());
        request.setVoided(Boolean.FALSE);
        return admissionRequestRepository.save(request);
    }

    public Page<AdmissionRequest> fetchAdmissionRequest(final String patientName,
                                                        final FullFillerStatusType status,
                                                        final String requestedByusername,
                                                        final Long wardId,
                                                        final DateRange requestDateRange,
                                                        Pageable pageable) {
        Specification<AdmissionRequest> s = AdmissionRequestSpecification.createSpecification(patientName, status,
                requestedByusername, wardId, requestDateRange);
        return admissionRequestRepository.findAll(s, pageable);

    }

    public Page<Admission> fetchAdmissions(final String admissionNo, final Long wardId, final Long roomId, final Long bedId, final String term, final Boolean discharged, final Boolean active, final Status status, final DateRange range, final Pageable pageable) {

        Ward ward = null;
        Room room = null;
        Bed bed = null;
        if (wardId != null) {
            ward = wardService.getWard(wardId);
        }
        if (roomId != null) {
            room = roomService.getRoom(roomId);
        }
        if (bedId != null) {
            bed = bedService.getBed(bedId);
        }
        Specification<Admission> s = AdmissionSpecification.createSpecification(admissionNo, ward, room, bed, term, discharged, active, status, range);
        return admissionRepository.findAll(s, pageable);
    }

    public Admission findAdmissionById(Long id) {
        if (id != null) {
            return admissionRepository.findById(id).orElseThrow(() -> APIException.notFound("Admission id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide admission id ", "");
        }
    }

    public Optional<Admission> findAdmissionByPatientAndStatus(String patientId) {
        Patient patient = patientService.findPatientOrThrow(patientId);
        return admissionRepository.findByPatientAndStatus(patient, Status.Admitted);

    }

    public Optional<Admission> findByAdmissionNo(String admissionNo) {
        return admissionRepository.findByAdmissionNo(admissionNo);

    }

    public Admission findAdmissionByNumber(String admissionNo) {
        if (admissionNo != null) {
            return admissionRepository.findByAdmissionNo(admissionNo).orElseThrow(() -> APIException.notFound("Admission number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }

    @Transactional
    public Admission updateAdmission(Long id, AdmissionData d) {
        Ward w = wardService.getWard(d.getWardId());
        Bed b = bedService.getBed(d.getBedId());
        BedType bt = bedService.getBedType(d.getBedTypeId());
        Patient p = patientService.findPatientOrThrow(d.getPatientNumber());

        Admission a = findAdmissionById(id);
        a.setWard(w);
        a.setBed(b);
        a.setBedType(bt);
        a.setPatient(p);
        if (d.getRoomId() != null) {
            Room room = roomService.getRoom(d.getRoomId());
            a.setRoom(room);
        }

        //visit data
        a.setStartDatetime(d.getAdmissionDate());
        a.setVisitType(VisitEnum.VisitType.Inpatient);
        a.setComments(d.getNarration());
        a.setScheduled(Boolean.FALSE);
        a.setIsActiveOnConsultation(Boolean.FALSE);

        //payment data
        Scheme scheme = null;
        if (d.getPaymentMethod().equals(PaymentMethod.Insurance)) {
            scheme = schemeService.fetchSchemeById(d.getPaymentDetailsData().getSchemeId());
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
            PaymentDetails pd = PaymentDetailsData.map(d.getPaymentDetailsData());

            pd.setScheme(scheme);
            pd.setPayer(scheme.getPayer());
            pd.setVisit(a);
            if (config.isPresent()) {
                pd.setCoPayCalcMethod(config.get().getCoPayType());
                pd.setCoPayValue(config.get().getCoPayValue());
            }

            paymentDetailsService.createPaymentDetails(pd);
            //create bill for copay
            if (config.isPresent() && config.get().getCoPayValue() > 0) {
                billingService.createCopay(new CopayData(a.getAdmissionNo(), d.getPaymentDetailsData().getSchemeId()));
            }
        }
        a.getBed().setStatus(Bed.Status.Available);
        b.setStatus(Bed.Status.Occupied);

        bedService.updateBed(a.getBed());
        bedService.updateBed(b);

        return admissionRepository.save(a);
    }

    public Admission saveAdmission(Admission admission) {
        return admissionRepository.save(admission);
    }
}
