package io.smarthealth.clinical.record.api;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.record.data.*;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.ListToPage;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.notification.service.NotificationEventPublisher;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.security.service.UserService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
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

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
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

    private final AuditTrailService auditTrailService;

    private final ServicePointService servicePointService;

    @Transactional
    @PostMapping("/visit/{visitNo}/doctor-request")
    @PreAuthorize("hasAuthority('create_doctorrequest')")
    public @ResponseBody
    ResponseEntity<?> createRequest(
            @PathVariable("visitNo") final String visitNumber,
            @RequestBody @Valid final List<DoctorRequestData> docRequestData
    ) {
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
                for (DoctorRequest dr : docRequest) {
                    if (dr.getFulfillerStatus().equals(FullFillerStatusType.Unfulfilled)) {
                        throw APIException.conflict("{0} has already been requested ", item.getItemName());
                    }
                }

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
            doctorRequest.setOrderDate(LocalDateTime.now());
            docRequests.add(doctorRequest);
            if (!requestType.contains(data.getRequestType())) {
                requestType.add(data.getRequestType());
            }

        }

        List<DoctorRequest> docReqs = requestService.createRequest(docRequests);
        for (DoctorRequest req : docReqs) {
            auditTrailService.saveAuditTrail("Consultation", "created a request for " + req.getItem().getItemName() + " for patient " + req.getPatient().getFullName());
        }

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
            auditTrailService.saveAuditTrail("Consultation", "created a request for " + specimens.get().getItemName() + " for patient " + specimens.get().getPatientData().getFullName());
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
            auditTrailService.saveAuditTrail("Consultation", "Deleted a request identified by id " + id);
            return ResponseEntity.ok("Successfully deleted");
        } else {
            throw APIException.notFound("Request Number {0} not found.", id);
        }
    }

    @GetMapping("/visit/{visitNo}/doctor-request")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<?> fetchAllRequestsByVisit(
            @PathVariable("visitNo") final String visitNo,
            @PathVariable(value = "pageNo", required = false) final Integer pageNo,
            @PathVariable(value = "pageSize", required = false) final Integer pageSize
    ) {
        Pageable pageable = PaginationUtil.createPage(pageNo, pageSize);
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
        auditTrailService.saveAuditTrail("Consultation", "Viewed requests identified by visitNo " + visitNo);
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
        auditTrailService.saveAuditTrail("Consultation", "Viewed past requests for patient " + patient.getFullName());
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
        Page<DoctorRequest> pageList = requestService.fetchAllDoctorRequests(visitNo, patientNo, requestType, fulfillerStatus, "patient", Pageable.unpaged(), activeVisit, term, range);
        /* Queue directly sent-  */

        ServicePointType pointType = null;
        if (requestType != null) {
            switch (requestType) {
                case Radiology:
                    pointType = ServicePointType.Radiology;
                    break;
                case Pharmacy:
                    pointType = ServicePointType.Pharmacy;
                    break;
                case Laboratory:
                    pointType = ServicePointType.Laboratory;
                    break;
                case Procedure:
                    pointType = ServicePointType.Procedure;
                    break;
                default:
                    pointType = null;
                    break;
            }
        }

        List<Visit> directVisits = new ArrayList<>();
        if (pointType != null) {
            //Find service point by request type (point type) - assuming there is only one service point of each type
            ServicePoint servicePoint = servicePointService.getServicePointByType(pointType);
            directVisits = visitService.visitByServicePointAndServedAtServicePoint(servicePoint, false);
        }

        /* End of queue directly sent */
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
        int count = waitingRequests.size();
        for (Visit v : directVisits) {
            count++;
            WaitingRequestsData waitingRequest = new WaitingRequestsData();
            waitingRequest.setPatientData(patientService.convertToPatientData(v.getPatient()));
            waitingRequest.setPatientNumber(v.getPatient().getPatientNumber());
            waitingRequest.setVisitData(visitService.convertVisitEntityToData(v));
            waitingRequest.setVisitNumber(v.getVisitNumber());
            waitingRequest.setRequestId(Long.valueOf(count));

            waitingRequest.setIsDirectSent(Boolean.TRUE);
            //no line items
            waitingRequest.setItem(new ArrayList<>());
            waitingRequests.add(waitingRequest);
        }
        System.out.println("Page" + page);
        System.out.println("size" + size);
        if (page != null) {
            page = page - 1;
        }
        Page<WaitingRequestsData> list = ListToPage.map(waitingRequests, page, size);

        PagedListHolder waitingPage = new PagedListHolder(waitingRequests);
        waitingPage.setPageSize(pageable.getPageSize()); // number of items per page
        waitingPage.setPage(pageable.getPageNumber());

        Pager<List<WaitingRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber());
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctor Requests");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Consultation", "Viewed all patient requests");
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
        auditTrailService.saveAuditTrail("Consultation", "Viewed all patient requests for visitNo " + visitNo);
        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/doc-request/{requestId}/status")
    @PreAuthorize("hasAuthority('edit_dispense')")
    public ResponseEntity<?> voidDocRequest(@PathVariable(value = "requestId") Long id) {
        Boolean status = requestService.voidRequest(id);
        auditTrailService.saveAuditTrail("Consultation", "Deleted patient request for identified by id " + id);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/doc-request/{id}")
    @PreAuthorize("hasAuthority('delete_doctorrequest')")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") final Long id) {
        auditTrailService.saveAuditTrail("Consultation", "Deleted patient request for identified by id " + id);
        return requestService.deleteById(id);
    }

    @GetMapping("/doctor-request/list")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<Pager<OrdersRequest>> getDoctorsOrders(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "fulfillerStatus", required = false) final FullFillerStatusType fulfillerStatus,
            @RequestParam(value = "paymentMethod", required = false) PaymentMethod paymentMethod,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<OrdersRequest> list = requestService.getDoctorOrderRequests(visitNumber, patientNumber, requestType, fulfillerStatus, paymentMethod, range, pageable)
                .map(OrdersRequest::of);
        auditTrailService.saveAuditTrail("Consultation", "Viewed all patient requests ");
        return ResponseEntity.ok((Pager<OrdersRequest>) PaginationUtil.toPager(list, "Doctors Requests"));
    }

    @GetMapping("/doctor-request/cash-request-summary")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<Pager<VisitOrderDTO>> getDoctorsCashRequestSummary(
            @RequestParam(value = "requestType", required = false) final RequestType requestType,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<VisitOrderDTO> list = requestService.getDoctorOrderSummary(requestType, range, pageable);
        return ResponseEntity.ok((Pager<VisitOrderDTO>) PaginationUtil.toPager(list, "Doctors Requests Summary"));
    }

    @GetMapping("/doctor-request/cash-request-summary/{visitNumber}/items")
    @PreAuthorize("hasAuthority('view_doctorrequest')")
    public ResponseEntity<List<VisitOrderItemDTO>> getDoctorsCashRequestItems(@PathVariable("visitNumber") final String visitNumber) {
        List<VisitOrderItemDTO> list = requestService.getDoctorOrderSummaryItems(visitNumber);
        return ResponseEntity.ok(list);
    }

}
