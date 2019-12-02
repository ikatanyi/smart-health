/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.lab.data.PatientLabTestData;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.service.LabResultsService;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.DoctorRequestItem;
import io.smarthealth.clinical.record.data.WaitingRequestsData;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.common.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to Doctor Requests/Orders maintenance")
public class DoctorRequestController {
    
    private final DoctorRequestService requestService;
    
    private final VisitService visitService;
    
    private final ModelMapper modelMapper;
    
    private final EmployeeService employeeService;
    
    private final SequenceService sequenceService;
    
    private final ItemService itemService;
    
    private final PatientQueueService patientQueueService;
    
    private final PatientService patientService;
    
    private final LabResultsService labResultsService;
    
    public DoctorRequestController(DoctorRequestService requestService, VisitService visitService, ModelMapper modelMapper, EmployeeService employeeService, SequenceService sequenceService, ItemService itemService, PatientQueueService patientQueueService, PatientService patientService, LabResultsService labResultsService) {
        this.requestService = requestService;
        this.visitService = visitService;
        this.modelMapper = modelMapper;
        this.employeeService = employeeService;
        this.sequenceService = sequenceService;
        this.itemService = itemService;
        this.patientQueueService = patientQueueService;
        this.patientService = patientService;
        this.labResultsService = labResultsService;
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
            //doctorRequest.setDoctorRequestItem(itemService);
            doctorRequest.setItem(item);
            doctorRequest.setItemCostRate(item.getCostRate());
            doctorRequest.setItemRate(item.getRate());
            doctorRequest.setPatient(visit.getPatient());
            doctorRequest.setVisit(visit);
            doctorRequest.setRequestedBy(employee);
            doctorRequest.setOrderNumber(orderNo);
            doctorRequest.setFulfillerStatus(DoctorRequest.FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setFulfillerComment(DoctorRequest.FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setRequestType(data.getRequestType().name());
            docRequests.add(doctorRequest);
        }
        
        List<DoctorRequest> docReqs = requestService.createRequest(docRequests);
        
        List<DoctorRequestData> requestList = modelMapper.map(docReqs, new TypeToken<List<DoctorRequest>>() {
        }.getType());
        
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
    
    @GetMapping("/doctor-request")
    public ResponseEntity<?> waitingListByRequestType(
            @RequestParam(value = "requestType", required = false) final String requestType,
            @RequestParam(value = "fulfillerStatus", required = false, defaultValue = "Unfulfilled") final String fulfillerStatus,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        //Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(null, requestType, fulfillerStatus, pageable);
        Page<DoctorRequest> pageList = requestService.fetchDoctorRequestLine(fulfillerStatus, requestType, pageable);
        List<WaitingRequestsData> waitingRequests = new ArrayList<>();
        
        for (DoctorRequest docReq : pageList.getContent()) {
            WaitingRequestsData waitingRequest = new WaitingRequestsData();
            waitingRequest.setPatientData(patientService.convertToPatientData(docReq.getPatient()));
            waitingRequest.setPatientNumber(docReq.getPatient().getPatientNumber());
            waitingRequest.setVisitData(visitService.convertVisitEntityToData(docReq.getVisit()));
            waitingRequest.setVisitNumber(docReq.getVisit().getVisitNumber());
            waitingRequest.setRequestId(docReq.getId());
            //find line items by request_id
            List<DoctorRequest> serviceItems = requestService.fetchServiceRequestsByPatient(docReq.getPatient(), fulfillerStatus, requestType);
            List<DoctorRequestItem> requestItems = new ArrayList<>();
            for (DoctorRequest r : serviceItems) {
                requestItems.add(DoctorRequestItem.map(r));
            }
            waitingRequest.setItem(requestItems);
            waitingRequests.add(waitingRequest);
        }
        
        PagedListHolder waitingPage = new PagedListHolder(waitingRequests);
        waitingPage.setPageSize(size); // number of items per page
        waitingPage.setPage(page);
        
        Pager<List<WaitingRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(waitingPage.getPageList());
        PageDetails details = new PageDetails();
        details.setPage(waitingPage.getPage() + 1);
        details.setPerPage(waitingPage.getPageSize());
        details.setTotalElements(Long.valueOf(waitingRequests.size()));
        details.setTotalPage(waitingPage.getPageCount());
        details.setReportName("Doctor Requests");
        pagers.setPageDetails(details);
        
        return ResponseEntity.ok(pagers);
    }
    
    @GetMapping("/visit/{visitNo}/doctor-request/{requestType}")
    public ResponseEntity<?> fetchAllRequestsByVisitAndRequestType(
            @PathVariable("visitNo") final String visitNo,
            @PathVariable("requestType") final String requestType,
            @RequestParam(value = "fulfillerStatus", required = false) final String fulfillerStatus,
            Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);

//        Page<DoctorRequest> page = requestService.findAllRequestsByOrderNoAndRequestType(visitNo, requestType, pageable);
        Page<DoctorRequest> page = requestService.fetchAllDoctorRequests(visit.getVisitNumber(), requestType, fulfillerStatus, pageable);
        
        Page<DoctorRequestData> list = page.map(r -> {
            DoctorRequestData dd = DoctorRequestData.map(r);
            dd.setEmployeeData(employeeService.convertEmployeeEntityToEmployeeData(r.getRequestedBy()));
            dd.setPatientNumber(r.getPatient().getPatientNumber());
            dd.setVisitNumber(visit.getVisitNumber());
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
    
    @DeleteMapping("/doc-request/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        return requestService.deleteById(id);
    }
    
    @GetMapping("/lab-results/{visitNo}")
    public ResponseEntity<?> fetchLabRequestByVisit(@PathVariable("visitNo") final String visitNo) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        List<PatientLabTest> results = labResultsService.findLabResultsByVisit(visit);
        return ResponseEntity.status(HttpStatus.OK).body(PatientLabTestData.mapConfirmedTests(results));
    }
    
}
