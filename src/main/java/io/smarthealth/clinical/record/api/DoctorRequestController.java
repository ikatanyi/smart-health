package io.smarthealth.clinical.record.api;

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
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.notification.service.NotificationEventPublisher;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import io.smarthealth.clinical.record.data.OrdersRequest;

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

    private final UserService userService;

    private final NotificationEventPublisher notificationEventPublisher;

    @Transactional
    @PostMapping("/visit/{visitNo}/doctor-request")
    @PreAuthorize("hasAuthority('create_doctorrequest')")
    public @ResponseBody
    ResponseEntity<?> createRequest(@PathVariable("visitNo") final String visitNumber, @RequestBody @Valid final List<DoctorRequestData> docRequestData) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        Optional<User> user = userService.findUserByUsernameOrEmail(SecurityUtils.getCurrentUserLogin().get());
        List<DoctorRequest> docRequests = new ArrayList<>();
        String orderNo = sequenceNumberService.next(1L, Sequences.DoctorRequest.name());
        List<DoctorRequestData.RequestType> requestType = new ArrayList<>();
        for (DoctorRequestData data : docRequestData) {
            DoctorRequest doctorRequest = DoctorRequestData.map(data);
            Item item = itemService.findById(Long.valueOf(data.getItemCode())).get();
            //validate if already requested
            List<DoctorRequest> docRequest = requestService.fetchRequestByVisitAndItem(visit, item);
            if (docRequest.size() > 0) {
                throw APIException.conflict("{0} has already been requested ", item.getItemName());
            }
            doctorRequest.setItem(item);
            doctorRequest.setItemCostRate(item.getCostRate() != null ? item.getCostRate().doubleValue() : 0);
            doctorRequest.setItemRate(item.getRate().doubleValue());
            doctorRequest.setPatient(visit.getPatient());
            doctorRequest.setVisit(visit);
            doctorRequest.setRequestedBy(user.get());
            doctorRequest.setOrderNumber(orderNo);
            doctorRequest.setFulfillerStatus(FullFillerStatusType.Unfulfilled);
            doctorRequest.setFulfillerComment(FullFillerStatusType.Unfulfilled.name());
            doctorRequest.setRequestType(data.getRequestType());
            doctorRequest.setOrderDate(LocalDate.now());
            docRequests.add(doctorRequest);
            if (!requestType.contains(data.getRequestType())) {
                requestType.add(data.getRequestType());
            }

        }

        List<DoctorRequest> docReqs = requestService.createRequest(docRequests);

        notificationEventPublisher.publishDocRequestEvent(requestType);

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
        Optional<DoctorRequestData> specimens = requestService.getDocRequestDataById(id);
        if (specimens != null) {
            return ResponseEntity.ok(specimens);
        } else {
            throw APIException.notFound("Request Number {0} not found.", id);
        }
    }

    @DeleteMapping("/doctor-request/{id}")
    @PreAuthorize("hasAuthority('delete_doctorrequest')")
    public ResponseEntity<?> deleteRequestById(@PathVariable("id") final Long id) {
        Optional<DoctorRequest> docRequest = requestService.getDocRequestById(id);

        if (docRequest.isPresent()) {
            if (docRequest.get().getFulfillerStatus().equals(FullFillerStatusType.Fulfilled)) {
                throw APIException.badRequest("You cannot remove a fulfilled request", "");
            }
            requestService.deleteDocRequest(docRequest.get());
            return ResponseEntity.ok("Successfully deleted");
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
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> pastDocRequests(
            @PathVariable(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false, defaultValue = "Unfulfilled") final FullFillerStatusType fulfillerStatus,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size,
            @RequestParam(value = "billPaymentValidation", required = false, defaultValue = "false") final Boolean billPaymentValidationPoint
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Patient patient = patientService.findPatientOrThrow(patientNo);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        //fetch all visits by patient
        Page<Visit> patientVisits = visitService.fetchAllVisits(null, null, null, patientNo, null, false, range, null, null, false, null, billPaymentValidationPoint, pageable);
        System.out.println("patientVisits " + patientVisits.getContent().size());
        List<HistoricalDoctorRequestsData> doctorRequestsData = new ArrayList<>();

        for (Visit v : patientVisits.getContent()) {
            System.out.println("Visit number " + v.getVisitNumber());
            System.out.println("Patient number " + patientNo);
            Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(v.getVisitNumber(), patientNo, requestType, fulfillerStatus, "patient", pageable, null, null, null);
            System.out.println("pageList " + pageList.getContent().size());
            int count = 0;
            for (DoctorRequest docReq : pageList.getContent()) {
                System.out.println("count " + count);
                HistoricalDoctorRequestsData waitingRequest = new HistoricalDoctorRequestsData();
                waitingRequest.setId(docReq.getId());
                waitingRequest.setPatientName(patient.getFullName());
                waitingRequest.setPatientNumber(patient.getPatientNumber());

                waitingRequest.setStartDate(v.getStartDatetime());
                waitingRequest.setStopDatetime(v.getStopDatetime());

                waitingRequest.setVisitNumber(v.getVisitNumber());
                waitingRequest.setVisitNotes(v.getComments());

                //find line items by request_id
                List<DoctorRequest> serviceItems = requestService.fetchServiceRequests(docReq.getPatient(), fulfillerStatus, requestType, v);
                System.out.println("serviceItems " + serviceItems.size());
                List<DoctorRequestItem> requestItems = new ArrayList<>();
                for (DoctorRequest r : serviceItems) {
                    requestItems.add(requestService.toData(r));
                }
                waitingRequest.setItem(requestItems);
                doctorRequestsData.add(waitingRequest);
                count++;
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
            @RequestParam(value = "activeVisit", required = false) final Boolean activeVisit,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "term", required = false) String term
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
//        final DateRange range = DateRange.fromIsoString(dateRange);
        Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(visitNo, patientNo, requestType, fulfillerStatus, "patient", pageable, activeVisit, term, range);
        List<WaitingRequestsData> waitingRequests = new ArrayList<>();

        for (DoctorRequest docReq : pageList.getContent()) {
            WaitingRequestsData waitingRequest = new WaitingRequestsData();
            waitingRequest.setPatientData(patientService.convertToPatientData(docReq.getPatient()));
            waitingRequest.setPatientNumber(docReq.getPatient().getPatientNumber());
            waitingRequest.setVisitData(visitService.convertVisitEntityToData(docReq.getVisit()));
            waitingRequest.setVisitNumber(docReq.getVisit().getVisitNumber());
            waitingRequest.setRequestId(docReq.getId());
            //find line items by request_id
            List<DoctorRequest> serviceItems = requestService.fetchServiceRequestsByVisit(docReq.getVisit(), fulfillerStatus, requestType);
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

        Page<DoctorRequest> page = requestService.fetchAllDoctorRequests(visit.getVisitNumber(), patientNo, requestType, fulfillerStatus, null, pageable, null, null, null);

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
    
    @PutMapping("/doc-request/{requestId}/status")
    @PreAuthorize("hasAuthority('edit_dispense')")
    public ResponseEntity<?> voidDocRequest(@PathVariable(value = "requestId") Long id) {
        Boolean status = requestService.voidRequest(id);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/doc-request/{id}")
    @PreAuthorize("hasAuthority('delete_doctorrequest')")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        return requestService.deleteById(id);
    }

    @GetMapping("/doctor-request/list")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<  Pager<OrdersRequest>> getDoctorsOrders(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false) final FullFillerStatusType fulfillerStatus,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<OrdersRequest> list = requestService.getDoctorOrderRequests(visitNumber, patientNumber, requestType, fulfillerStatus, range, pageable)
                .map(OrdersRequest::of); 
        
        return ResponseEntity.ok((Pager<OrdersRequest>) PaginationUtil.toPager(list, "Doctors Requests"));
    }
}
