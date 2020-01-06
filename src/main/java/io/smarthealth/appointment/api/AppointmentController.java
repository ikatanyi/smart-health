/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.api;

import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.appointment.data.AppointmentTypeData;
import io.smarthealth.appointment.domain.Appointment;
import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.service.AppointmentService;
import io.smarthealth.appointment.service.AppointmentTypeService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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
        AppointmentType appointmentType = this.appointmentTypeService.createAppointmentType(convertDataToAppType(appointmentTypeData));

        AppointmentTypeData savedAppointmentTypeData = convertDataToAppTypeData(appointmentType);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/appointmentTypes/{id}")
                .buildAndExpand(appointmentType.getId()).toUri();

        //return ResponseEntity.created(location).body(savedAppointmentTypeData);
        return ResponseEntity.created(location).body(ApiResponse.successMessage("Appointment type was successfully created", HttpStatus.CREATED, savedAppointmentTypeData));
    }

    @PostMapping("/appointment")
    public @ResponseBody
    ResponseEntity<?> createAppointment(@RequestBody @Valid final AppointmentData appointmentData) {
        System.out.println("appointmentData " + appointmentData);

        appointmentData.setStatus(AppointmentData.Status.Scheduled);
        Appointment appointment = this.appointmentService.createAppointment(appointmentData);

        AppointmentData savedAppointmentData = appointmentService.convertAppointment(appointment);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/appointment/{no}")
                .buildAndExpand(appointment.getAppointmentNo()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.successMessage("Appointment was successfully created", HttpStatus.CREATED, savedAppointmentData));
    }

    @GetMapping("/employee/{no}/appointment")
    public @ResponseBody
    ResponseEntity<?> fetchAppointmentsByServiceProvider(@PathVariable("no") final String staffNo, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Employee employee = employeeService.fetchEmployeeByNumberOrThrow(staffNo);
        Page<Appointment> page = appointmentService.fetchAllAppointmentsByPractioneer(employee, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/appointment")
    public @ResponseBody
    ResponseEntity<?> fetchAllAppointments(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<Appointment> page = appointmentService.fetchAllAppointments(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/appointment/{appointmentNo}")
    public @ResponseBody
    ResponseEntity<?> fetchAppointmentByappointmentNo(@PathVariable("appointmentNo") final String appointmentNo) {

        Appointment appointment = appointmentService.fetchAppointmentByNo(appointmentNo);

        return new ResponseEntity<>(appointmentService.convertAppointment(appointment), HttpStatus.OK);
    }

    //fetchAllAppointmentTypes
    @GetMapping("/appointmentTypes")
    public ResponseEntity<List<AppointmentTypeData>> fetchAllAppTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<AppointmentTypeData> page = appointmentTypeService.fetchAllAppointmentTypes(pageable).map(a -> convertDataToAppTypeData(a));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/appointmentTypes/{id}")
    public ResponseEntity<AppointmentTypeData> fetchAppTypeById(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable, @PathVariable("id") final Long id) {
        System.out.println("Id of app type " + id);
        AppointmentTypeData appointmentTypeData = convertDataToAppTypeData(appointmentTypeService.fetchAppointmentTypeById(id));
        return ResponseEntity.ok(appointmentTypeData);
    }

    @PutMapping("/appointmentTypes/{id}")
    public ResponseEntity<AppointmentTypeData> fetchAppTypeById(@RequestBody @Valid final AppointmentTypeData appointmentTypeD, @PathVariable("id") final Long id) {
        AppointmentType appointmentType = appointmentTypeService.fetchAppointmentTypeById(id);
        appointmentType.setColor(appointmentTypeD.getColor());
        appointmentType.setDuration(appointmentTypeD.getDuration());
        appointmentType.setName(appointmentTypeD.getName());
        AppointmentTypeData appointmentTypeData = convertDataToAppTypeData(appointmentTypeService.createAppointmentType(appointmentType));
        return ResponseEntity.ok(appointmentTypeData);
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
