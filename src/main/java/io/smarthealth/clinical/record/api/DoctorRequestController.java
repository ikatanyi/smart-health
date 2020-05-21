package io.smarthealth.clinical.record.api;

import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.DoctorRequestItem;
import io.smarthealth.clinical.record.data.HistoricalDoctorRequestsData;
import io.smarthealth.clinical.record.data.WaitingRequestsData;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.clinical.PatientVisitData;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to Doctor Requests/Orders maintenance")
@RequiredArgsConstructor
public class DoctorRequestController {

    private final DoctorRequestService requestService;

    private final VisitService visitService;

    private final ModelMapper modelMapper;

    private final ItemService itemService;

    private final PatientService patientService;

    private final SequenceNumberService sequenceNumberService;

    private final PricelistService pricelist;

    private final UserService userService;

    @PostMapping("/visit/{visitNo}/doctor-request")
    @PreAuthorize("hasAuthority('create_doctorrequest')")
    public @ResponseBody
    ResponseEntity<?> createRequest(@PathVariable("visitNo") final String visitNumber, @RequestBody @Valid final List<DoctorRequestData> docRequestData) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        Optional<User> user = userService.findUserByUsernameOrEmail(SecurityUtils.getCurrentUserLogin().get());

//        Employee employee = employeeService.findEmployeeByUsername(SecurityUtils.getCurrentUserLogin().get()).orElse(null);
        List<DoctorRequest> docRequests = new ArrayList<>();
        String orderNo = sequenceNumberService.next(1L, Sequences.DoctorRequest.name());
        for (DoctorRequestData data : docRequestData) {
            DoctorRequest doctorRequest = DoctorRequestData.map(data);
            Item item = itemService.findById(Long.valueOf(data.getItemCode())).get();
//            doctorRequest.setDoctorRequestItem(itemService);
            doctorRequest.setItem(item);
            //doctorRequest.setItemCostRate(item.getCostRate().doubleValue());
            doctorRequest.setItemCostRate(item.getCostRate() != null ? item.getCostRate().doubleValue() : 0);
            doctorRequest.setItemRate(item.getRate().doubleValue());
            doctorRequest.setPatient(visit.getPatient());
            doctorRequest.setVisit(visit);
            doctorRequest.setRequestedBy(user.get());
            doctorRequest.setOrderNumber(orderNo);
            doctorRequest.setFulfillerStatus(FullFillerStatusType.Unfulfilled);
            doctorRequest.setFulfillerComment(FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setRequestType(data.getRequestType());
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
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> fetchRequestById(@PathVariable("id") final Long id) {
        Optional<DoctorRequestData> specimens = requestService.getDocRequestById(id);
        if (specimens != null) {
            return ResponseEntity.ok(specimens);
        } else {
            throw APIException.notFound("Request Number {0} not found.", id);
        }
    }

    @GetMapping("/visit/{visitNo}/doctor-request")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> fetchAllRequestsByVisit(@PathVariable("visitNo") final String visitNo, Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        Page<DoctorRequest> page = requestService.findAllRequestsByVisit(visit, pageable);

        Page<DoctorRequestData> list = page.map(r -> {
            DoctorRequestData dd = DoctorRequestData.map(r);
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

    @GetMapping("/doctor-request/{patientNo}/past")
    public ResponseEntity<?> pastDocRequests(
            @PathVariable(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false, defaultValue = "Unfulfilled") final FullFillerStatusType fulfillerStatus,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        //fetch all visits by patient
        Page<Visit> patientVisits = visitService.fetchVisitByPatientNumber(patientNo, pageable);
        List<HistoricalDoctorRequestsData> doctorRequestsData = new ArrayList<>();

        for (Visit v : patientVisits.getContent()) {
            Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(v.getVisitNumber(), patientNo, requestType, fulfillerStatus, "patient", pageable);

            for (DoctorRequest docReq : pageList.getContent()) {
                HistoricalDoctorRequestsData waitingRequest = new HistoricalDoctorRequestsData();
                waitingRequest.setPatientName(docReq.getPatient().getFullName());
                waitingRequest.setPatientNumber(docReq.getPatient().getPatientNumber());

                waitingRequest.setStartDate(docReq.getVisit().getStartDatetime());
                waitingRequest.setStopDatetime(docReq.getVisit().getStopDatetime());

                waitingRequest.setVisitNumber(docReq.getVisit().getVisitNumber());
                //find line items by request_id
                List<DoctorRequest> serviceItems = requestService.fetchServiceRequestsByPatient(docReq.getPatient(), fulfillerStatus, requestType);
                List<DoctorRequestItem> requestItems = new ArrayList<>();
                for (DoctorRequest r : serviceItems) {
                    requestItems.add(requestService.toData(r));
                }
                waitingRequest.setItem(requestItems);
                doctorRequestsData.add(waitingRequest);
            }

        }

        PagedListHolder waitingPage = new PagedListHolder(doctorRequestsData);
        waitingPage.setPageSize(pageable.getPageSize()); // number of items per page
        waitingPage.setPage(pageable.getPageNumber());

        Pager<List<HistoricalDoctorRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(waitingPage.getPageList());
        PageDetails details = new PageDetails();
        details.setPage(waitingPage.getPage() + 1);
        details.setPerPage(waitingPage.getPageSize());
        details.setTotalElements(Long.valueOf(doctorRequestsData.size()));
        details.setTotalPage(waitingPage.getPageCount());
        details.setReportName("History Doctor Requests");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/doctor-request")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> waitingListByRequestType(
            @RequestParam(value = "visitNo", required = false) final String visitNo,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false, defaultValue = "Unfulfilled") final FullFillerStatusType fulfillerStatus,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(visitNo, patientNo, requestType, fulfillerStatus, "patient", pageable);
        //Page<DoctorRequest> pageList = requestService.fetchDoctorRequestLine(fulfillerStatus, requestType, pageable);
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
                requestItems.add(requestService.toData(r));
            }
            waitingRequest.setItem(requestItems);
            waitingRequests.add(waitingRequest);
        }

        PagedListHolder waitingPage = new PagedListHolder(waitingRequests);
        waitingPage.setPageSize(pageable.getPageSize()); // number of items per page
        waitingPage.setPage(pageable.getPageNumber());

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
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> fetchAllRequestsByVisitAndRequestType(
            @PathVariable("visitNo") final String visitNo,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @PathVariable("requestType") final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false) final FullFillerStatusType fulfillerStatus,
            Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);

//        Page<DoctorRequest> page = requestService.findAllRequestsByOrderNoAndRequestType(visitNo, requestType, pageable);
        Page<DoctorRequest> page = requestService.fetchAllDoctorRequests(visit.getVisitNumber(), patientNo, requestType, fulfillerStatus, null, pageable);

        Page<DoctorRequestData> list = page.map(r -> {
            DoctorRequestData dd = DoctorRequestData.map(r);
//            dd.setEmployeeData(employeeService.convertEmployeeEntityToEmployeeData(r.getRequestedBy()));
//            dd.setPatientNumber(r.getPatient().getPatientNumber());
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
    @PreAuthorize("hasAuthority('delete_doctorrequest')")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        return requestService.deleteById(id);
    }

}
