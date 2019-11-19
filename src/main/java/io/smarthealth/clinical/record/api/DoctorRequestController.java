/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to Doctor Requests/Orders maintenance")
public class DoctorRequestController {

    final DoctorRequestService requestService;

    final VisitService visitService;

    final ModelMapper modelMapper;

    final EmployeeService employeeService;

    final SequenceService sequenceService;

    final ItemService itemService;

    final PatientQueueService patientQueueService;

    public DoctorRequestController(DoctorRequestService requestService, VisitService visitService, ModelMapper modelMapper, EmployeeService employeeService, SequenceService sequenceService, ItemService itemService, PatientQueueService patientQueueService) {
        this.requestService = requestService;
        this.visitService = visitService;
        this.modelMapper = modelMapper;
        this.employeeService = employeeService;
        this.sequenceService = sequenceService;
        this.itemService = itemService;
        this.patientQueueService = patientQueueService;
    }

    @PostMapping("/visit/{visitNo}/doctor-request")
    public @ResponseBody
    ResponseEntity<?> createRequest(@PathVariable("visitNo") final String visitNumber, @RequestBody @Valid final List<DoctorRequestData> docRequestData) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Employee employee = employeeService.fetchEmployeeByAccountUsername(SecurityUtils.getCurrentUserLogin().get());
        List<DoctorRequest> docRequests = new ArrayList<>();
        String orderNo = sequenceService.nextNumber(SequenceType.DoctorRequestNumber);
        for (DoctorRequestData data : docRequestData) {
            DoctorRequest doctorRequest = DoctorRequestData.map(data);
            Item item = itemService.findById(Long.valueOf(data.getItemCode())).get();
            doctorRequest.setItem(item);
            doctorRequest.setPatient(visit.getPatient());
            doctorRequest.setVisit(visit);
            doctorRequest.setRequestedBy(employee);
            doctorRequest.setOrderNumber(orderNo);
            doctorRequest.setFulfillerStatus(DoctorRequest.FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setFulfillerComment(DoctorRequest.FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setRequestType(data.getRequestType().name());
            docRequests.add(doctorRequest);
        }
        List<DoctorRequestData> requestList = requestService.createRequest(docRequests);
        if (requestList != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(requestList);
        } else {
            throw APIException.notFound("TestType Number {0} not found.", "");
        }
    }

    @GetMapping("/doctor-request/{id}")
    public ResponseEntity<?> fetchRequestById(@PathVariable("id") final Long id) {
        Optional<DoctorRequestData> specimens = requestService.getDocRequestById(id);
        if (specimens != null) {
            return ResponseEntity.ok(specimens);
        } else {
            throw APIException.notFound("Request Number {0} not found.", id);
        }
    }

    @GetMapping("/visit/{visitNo}/doctor-request")
    public ResponseEntity<?> fetchAllRequestsByVisit(@PathVariable("visitNo") final String visitNo, Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        Page<DoctorRequest> page = requestService.findAllRequestsByVisit(visit, pageable);

        Page<DoctorRequestData> list = page.map(r -> {
            DoctorRequestData dd = DoctorRequestData.map(r);
            dd.setEmployeeData(employeeService.convertEmployeeEntityToEmployeeData(r.getRequestedBy()));
            dd.setPatientNumber(r.getPatient().getPatientNumber());
            dd.setVisitNumber(visit.getVisitNumber());
            if (r.getItem() != null) {
                dd.setItemCode(r.getItem().getItemCode());
            }
            return dd;
        });
        Pager<List<DoctorRequestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctor Requests");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/visit/{visitNo}/doctor-request/{requestType}")
    public ResponseEntity<?> fetchAllRequestsByVisitAndRequestType(@PathVariable("visitNo") final String visitNo, @PathVariable("requestType") final String requestType, Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        Page<DoctorRequest> page = requestService.findAllRequestsByOrderNoAndRequestType(visitNo, requestType, pageable);

        Page<DoctorRequestData> list = page.map(r -> {
            DoctorRequestData dd = DoctorRequestData.map(r);
            dd.setEmployeeData(employeeService.convertEmployeeEntityToEmployeeData(r.getRequestedBy()));
            dd.setPatientNumber(r.getPatient().getPatientNumber());
            dd.setVisitNumber(visit.getVisitNumber());
            if (r.getItem() != null) {
                dd.setItemCode(r.getItem().getItemCode());
            }
            return dd;
        });
        Pager<List<DoctorRequestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctor Requests");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/docRequest")
    public ResponseEntity<List<DoctorRequestData>> fetchAllSpecimens(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        String visitNumber = queryParams.getFirst("visitNumber");
        String status = queryParams.getFirst("status");
        String requestType = queryParams.getFirst("requestType");
        String from = queryParams.getFirst("from");
        String to = queryParams.getFirst("to");
        List<DoctorRequestData> page = requestService.findAll(visitNumber, status, requestType, from, to, pageable);
        HttpHeaders headers = null;//PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page.);
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }

    @DeleteMapping("/docRequest/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        return requestService.deleteById(id);
    }

}
