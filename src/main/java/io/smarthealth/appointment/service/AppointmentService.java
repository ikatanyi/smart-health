package io.smarthealth.appointment.service;

import io.smarthealth.appointment.data.AppRescheduleData;
import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.appointment.domain.AppointmentRepository;
import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.domain.enumeration.StatusType;
import io.smarthealth.appointment.domain.specification.AppointmentSpecification;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.clinical.admission.service.DischargeService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.notification.data.SmsMessageData;
import io.smarthealth.notification.domain.enumeration.ReceiverType;
import io.smarthealth.notification.service.SmsMessagingService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.PersonRepository;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PersonRepository personRepository;
    private final PatientRepository patientRepository;
    private final AppointmentTypeService appointmentTypeService;
    private final EmployeeService employeeService;
    private final ItemService itemService;
    private final SmsMessagingService messagingService;
    private final VisitService visitService;
    private DischargeService dischargeService;

    @Autowired
    ModelMapper modelMapper;

    public ContentPage<AppointmentData> fetchAppointmentsByPatient(final String patientNumber, final Pageable pageable) {
        //Validate if Patient exists
        if (!this.patientRepository.existsByPatientNumber(patientNumber)) {
            throw APIException.notFound("Patient with the identity {0} does not exist..", patientNumber);
        }
        //Fetch patient entity by patient number
        final Patient patientEntity = this.patientRepository.findByPatientNumber(patientNumber).get();
        //if patient exists return appointments
        Page<Appointment> appointments = appointmentRepository.findByPatient(patientEntity, pageable);
        final ContentPage<AppointmentData> appointmentPage = new ContentPage();
        appointmentPage.setTotalPages(appointments.getTotalPages());
        appointmentPage.setTotalElements(appointments.getTotalElements());
        if (appointments.getSize() > 0) {
            final ArrayList<AppointmentData> appointmentlist = new ArrayList<>(appointments.getSize());
            appointmentPage.setContents(appointmentlist);
            appointments.forEach((appointment) -> appointmentlist.add(AppointmentData.map(appointment)));
        }
        return appointmentPage;
    }

    public Appointment createAppointment(AppointmentData appointment) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        List<SmsMessageData>dataList = new ArrayList();
        SmsMessageData msgData = new SmsMessageData();
        
        AppointmentType appointmentType = appointmentTypeService.fetchAppointmentTypeWithNoFoundDetection(appointment.getAppointmentTypeId());
        Appointment entity = modelMapper.map(appointment, Appointment.class);
        //Verify patient number
        Optional<Patient> patient = patientRepository.findByPatientNumber(appointment.getPatientNumber());
        if (patient.isPresent()) {
            entity.setPatient(patient.get());
        }
        Optional<Employee> practitioner = employeeService.findEmployeeByStaffNumber(appointment.getPractitionerCode());
        if (practitioner.isPresent()) {
            entity.setPractitioner(practitioner.get());
            msgData = new SmsMessageData();
            msgData.setReceiverId(practitioner.get().getStaffNumber());
            msgData.setReceiverType(ReceiverType.employee);
            msgData.setMessage("Dear Doctor,"+practitioner.get().getFullName()+" You Have a scheduled Appoinment with "+patient.get().getFullName()+" on "+entity.getAppointmentDate()+" at"+entity.getStartTime());
            dataList.add(msgData);
        }        
        if (patient.isPresent()) {
            msgData = new SmsMessageData();
            msgData.setReceiverId(patient.get().getPatientNumber());
            msgData.setReceiverType(ReceiverType.patient);
            msgData.setMessage("Dear "+patient.get().getFullName()+", You Have a scheduled Appoinment with "+(practitioner.isPresent()?practitioner.get().getFullName():"")+" on"+entity.getAppointmentDate()+" at"+entity.getStartTime());
            dataList.add(msgData);
        }
        
        Optional<Item> service = itemService.findByItemCode(appointment.getProcedureCode());
        if (service.isPresent()) {
            entity.setService(service.get());
        }

        entity.setAppointmentType(appointmentType);

        Appointment savedAppointment = appointmentRepository.save(entity);

        messagingService.createBatchTextMessage(dataList);
        //check if discharge
        if (patient.isPresent()) {
            Optional<Visit> visit = visitService.patientActiveVisit(patient.get().getId());
            if (visit.isPresent()) {
                Visit v = visit.get();
                if(v.getVisitType().equals(VisitEnum.VisitType.Inpatient)){
                    //find discharge by visit
                   DischargeSummary discharge=  dischargeService.getDischargeByVisit(v.getVisitNumber());
                    discharge.setReviewDate(savedAppointment.getAppointmentDate());
                    dischargeService.saveDischargeSummary(discharge);
                }
            }

        }
        
        return savedAppointment;

    }

    public Appointment UpdateAppointment(Long id, AppointmentData appointment) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Appointment app = findAppointmentOrThrowException(id);

        Optional<Employee> practitioner = employeeService.findEmployeeByStaffNumber(appointment.getPractitionerCode());

        if (appointment.getAppointmentTypeId() != null) {
            Optional<AppointmentType> appointmentType = appointmentTypeService.fetchAppointmentTypeById(appointment.getAppointmentTypeId());
            if (appointmentType.isPresent()) {
                app.setAppointmentType(appointmentType.get());
            }
        }
        Optional<Item> procedure = itemService.findByItemCode(appointment.getProcedureCode());
        //Verify patient number
        Optional<Patient> patient = patientRepository.findByPatientNumber(appointment.getPatientNumber());
        if (patient.isPresent()) {
            app.setPatient(patient.get());
        }
//        Appointment entity = modelMapper.map(appointment, Appointment.class);
        app.setAllDay(appointment.getAllDay());
        app.setAppointmentDate(appointment.getAppointmentDate());
//        app.setAppointmentNo();
        app.setComments(appointment.getComments());
//        app.setDepartment(appointment.getD);
        app.setEndTime(appointment.getEndTime());
        app.setFirstName(appointment.getFirstName());
        app.setGender(appointment.getGender());
        app.setLastName(appointment.getLastName());
        app.setPhoneNumber(appointment.getPhoneNumber());
        if (practitioner.isPresent()) {
            app.setPractitioner(practitioner.get());
        }
        if (procedure.isPresent()) {
            app.setService(procedure.get());
        }
        app.setStartTime(appointment.getStartTime());
        app.setStatus(appointment.getStatus());
        app.setUrgency(appointment.getUrgency());

        Optional<Patient> patientEntity = this.patientRepository.findByPatientNumber(appointment.getPatientNumber());
        if (patientEntity.isPresent()) {
            app.setPatient(patientEntity.get());
        }

        Appointment savedAppointment = appointmentRepository.save(app);
        return savedAppointment;

    }

    public Appointment rescheduleAppointment(Long id, AppRescheduleData data) {
        Appointment appointment = findAppointmentOrThrowException(id);
        appointment.setStatus(StatusType.Rescheduled);

        appointmentRepository.save(appointment);
        Appointment newAppointment = new Appointment();

        newAppointment.setAllDay(appointment.getAllDay());
        newAppointment.setAppointmentDate(appointment.getAppointmentDate());
        newAppointment.setAppointmentType(newAppointment.getAppointmentType());
        newAppointment.setComments(appointment.getComments());
        newAppointment.setEndTime(appointment.getEndTime());
        newAppointment.setFirstName(appointment.getFirstName());
        newAppointment.setGender(appointment.getGender());
        newAppointment.setLastName(appointment.getLastName());
        newAppointment.setPhoneNumber(appointment.getPhoneNumber());
        newAppointment.setStartTime(appointment.getStartTime());
        newAppointment.setStatus(appointment.getStatus());
        newAppointment.setUrgency(appointment.getUrgency());
        if (newAppointment.getService()!= null) {
            newAppointment.setService(newAppointment.getService());
        }
        if (newAppointment.getPractitioner() != null) {
            newAppointment.setPractitioner(newAppointment.getPractitioner());
        }

        newAppointment.setStartTime(data.getStartTime());
        newAppointment.setEndTime(data.getEndTime());
        newAppointment.setAppointmentDate(data.getAppointmentDate());
        newAppointment.setStatus(StatusType.Scheduled);
        return appointmentRepository.save(newAppointment);

    }

    public Appointment fetchAppointmentByNo(final String appointmentNo) {
        return appointmentRepository.findByAppointmentNo(appointmentNo).orElseThrow(() -> APIException.notFound("Appointment identified by {0} not found", appointmentNo));
    }

    public Page<Appointment> fetchAllAppointments(String practitionerNumber, String patientId, String status, String deptCode, String urgency, String patientName, DateRange range, final Pageable pageable) {
        Specification<Appointment> spec = AppointmentSpecification.createSpecification(practitionerNumber, patientId, statusToEnum(status), deptCode, urgency, patientName, range);
        return appointmentRepository.findAll(spec, pageable);
    }

    private StatusType statusToEnum(String status) {
        if (status == null) {
            return null;
        }
        if (EnumUtils.isValidEnum(StatusType.class, status)) {
            return StatusType.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Appointment Status");
    }

    public Page<Appointment> fetchAllAppointmentsByPractioneer(final Employee employee, final Pageable pageable) {
        return appointmentRepository.findByPractitioner(employee, pageable);
    }

    public AppointmentData geAppointmentById(Long id) {
        Optional<Appointment> appointment = this.appointmentRepository.findById(id);

        return appointment.map(AppointmentData::map).orElse(null);
    }

    private Patient findPatientOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number : {0} not found.", patientNumber));
    }

    public Appointment findAppointmentOrThrowException(Long id) {
        return this.appointmentRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Transaction with id {0} not found.", id));
    }

//    private void throwIfAppointmentStatusInvalid(PaymentMode status) {
//        try {
//            PaymentMode.valueOf(status);
//        } catch (Exception ex) {
//            throw APIException.badRequest("Appointment PaymentMode : {0} is not supported .. ", status);
//        }
//    }
//    public AppointmentData convertAppointment(Appointment appointment) {
//        AppointmentData appData = modelMapper.map(appointment, AppointmentData.class);
//        if (appointment.getAppointmentType() != null) {
//            appData.setTypeOfAppointment(appointment.getAppointmentType().getName());
//            appData.setAppointmentTypeId(appointment.getAppointmentType().getId());
//        }
//        return appData;
//    }
}
