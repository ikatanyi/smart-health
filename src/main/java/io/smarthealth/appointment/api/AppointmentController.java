/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.api;

import io.smarthealth.appointment.data.AppRescheduleData;
import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.appointment.data.AppointmentTypeData;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.domain.enumeration.StatusType;
import io.smarthealth.appointment.service.AppointmentService;
import io.smarthealth.appointment.service.AppointmentTypeService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Appointment Controller", description = "Operations pertaining to appointments")
public class AppointmentController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AppointmentTypeService appointmentTypeService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/appointmentTypes")
    public @ResponseBody
    ResponseEntity<?> createAppointmentType(@RequestBody @Valid final AppointmentTypeData appointmentTypeData) {        
        AppointmentType result = appointmentTypeService.createAppointmentType(appointmentTypeData);        
        Pager<AppointmentTypeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("AppointmentType made successful");
        pagers.setContent(appointmentTypeService.toData(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/appointment")
    public @ResponseBody
    ResponseEntity<?> createAppointment(@RequestBody @Valid final AppointmentData appointmentData) {
        System.out.println("appointmentData " + appointmentData);

        appointmentData.setStatus(StatusType.Scheduled);
        Appointment appointment = this.appointmentService.createAppointment(appointmentData);

        AppointmentData savedAppointmentData = AppointmentData.map(appointment);

        Pager<AppointmentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Appointment made successfully");
        pagers.setContent(savedAppointmentData);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PutMapping("/appointment/{id}")
    public @ResponseBody
    ResponseEntity<?> updateAppointment(@RequestBody @Valid final AppointmentData appointmentData, @PathVariable("id") final Long id) {
        Appointment appointment = this.appointmentService.UpdateAppointment(id, appointmentData);

        AppointmentData savedAppointmentData = AppointmentData.map(appointment);

        Pager<AppointmentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Appointment Updated successfully");
        pagers.setContent(savedAppointmentData);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }
    
    @PostMapping("/appointment/{id}/reschedule")
    public @ResponseBody
    ResponseEntity<?> updateAppointment(@PathVariable("id") final Long id,
            @RequestBody @Valid final AppRescheduleData data) {
        Appointment appointment = this.appointmentService.rescheduleAppointment(id, data);

        AppointmentData savedAppointmentData = AppointmentData.map(appointment);

        Pager<AppointmentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Appointment rescheduled successful");
        pagers.setContent(savedAppointmentData);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

//    @GetMapping("/employee/{no}/appointment")
//    public @ResponseBody
//    ResponseEntity<?> fetchAppointmentsByServiceProvider(@PathVariable("no") final String staffNo, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
//
//        Employee employee = employeeService.fetchEmployeeByNumberOrThrow(staffNo);
//        Page<Appointment> page = appointmentService.fetchAllAppointmentsByPractioneer(employee, pageable);
//
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

    @GetMapping("/appointment")
    public @ResponseBody
    ResponseEntity<?> fetchAllAppointments(
        @RequestParam(value = "practitionerNumber", required = false) final String practitionerNumber,
        @RequestParam(value = "item", required = false) final String item,
        @RequestParam(value = "patientId", required = false) final String patientId,
        @RequestParam(value = "status", required = false) final String status,
        @RequestParam(value = "urgency", required = false) final String urgency,
        @RequestParam(value = "deptCode", required = false) final String deptCode,
        @RequestParam(value = "dateRange", required = false) String dateRange,  
        
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        System.err.println("my current statys "+status);
        Page<AppointmentData> results = appointmentService.fetchAllAppointments(practitionerNumber, patientId, status, deptCode, urgency, range, pageable).map(a->AppointmentData.map(a));
        
        Pager pager = new Pager();
        pager.setCode("200");
        pager.setContent(results.getContent());
        pager.setMessage("Appointment Types fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Appointment Types fetched");
//        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
        pager.setPageDetails(details);
        return ResponseEntity.ok(pager);

//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        
//        AppointmentData app = page.map(a->AppointmentData.map(a));//AppointmentData.map(appointment);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/appointment/{appointmentNo}")
    public @ResponseBody
    ResponseEntity<?> fetchAppointmentByappointmentNo(@PathVariable("appointmentNo") final String appointmentNo) {

        Appointment appointment = appointmentService.fetchAppointmentByNo(appointmentNo);

        return new ResponseEntity<>(AppointmentData.map(appointment), HttpStatus.OK);
    }

    //fetchAllAppointmentTypes
    @GetMapping("/appointmentTypes")
    public ResponseEntity<?> fetchAllAppTypes(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<AppointmentTypeData> results = appointmentTypeService.fetchAllAppointmentTypes(pageable).map(a -> appointmentTypeService.toData(a));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);

        Pager<List<AppointmentTypeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(results.getContent());
        PageDetails details = new PageDetails();
        details.setReportName("Appointment Types");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/appointmentTypes/{id}")
    public ResponseEntity<AppointmentTypeData> fetchAppTypeById(@PathVariable("id") final Long id) {
        AppointmentTypeData appointmentTypeData = appointmentTypeService.toData(appointmentTypeService.fetchAppointmentTypeWithNoFoundDetection(id));
        return ResponseEntity.ok(appointmentTypeData);
    }

    @PutMapping("/appointmentTypes/{id}")
    public ResponseEntity<?> fetchAppTypeById(@PathVariable("id") final Long id,@RequestBody @Valid final AppointmentTypeData appointmentTypeD) {
        AppointmentType result = appointmentTypeService.updateAppointmentType(id, appointmentTypeD);
        Pager<AppointmentTypeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("AppointmentType made successful");
        pagers.setContent(appointmentTypeService.toData(result));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

//    @GetMapping("/appointments")
//    public ResponseEntity<List<AppointmentData>> fetchAllAppointments(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
//        Page<AppointmentData> page = appointmentService.fetchAllAppointments(pageable).map(a -> appointmentService.convertAppointment(a));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }
    @DeleteMapping("/appointmentTypes/{id}")
    public @ResponseBody
    ResponseEntity<ApiResponse> deleteAppointmentType(@PathVariable("id") final Long appId) {
        try {
            this.appointmentTypeService.removeAppointmentTypeById(appId);
            return ResponseEntity.accepted().body(ApiResponse.successMessage("Appointment type was successfully deleted", HttpStatus.ACCEPTED, null));
        } catch (Exception ex) {
            Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            throw APIException.internalError("Error occured when deleting appointment type identifed by " + appId, ex.getMessage());
        }
    }

    private AppointmentType convertDataToAppType(AppointmentTypeData appointmentTypeData) {
        return modelMapper.map(appointmentTypeData, AppointmentType.class);
    }

    private AppointmentTypeData convertDataToAppTypeData(AppointmentType appointmentType) {
        return modelMapper.map(appointmentType, AppointmentTypeData.class);
    }

}
