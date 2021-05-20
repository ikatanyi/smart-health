/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.api;

import io.smarthealth.accounting.pettycash.data.PettyCashProcessedItemsData;
import io.smarthealth.accounting.pettycash.data.PettyCashRequestItemsData;
import io.smarthealth.accounting.pettycash.data.PettyCashRequestsData;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovedItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashItemsRepository;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashRequestsRepository;
import io.smarthealth.accounting.pettycash.service.PettyCashApprovalsService;
import io.smarthealth.accounting.pettycash.service.PettyCashRequestsService;
import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.approval.domain.ModuleApprovers;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;
import io.smarthealth.approval.service.ApprovalConfigService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@RestController
@Api("End points managing petty cash request module")
@RequestMapping("/api")
public class PettyCashRequestController {

    @Autowired
    PettyCashRequestsService pettyCashRequestsService;

    @Autowired
    ApprovalConfigService approvalConfigService;

    @Autowired
    UserService service;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    PettyCashApprovalsService approvalsService;

    @Autowired
    PettyCashItemsRepository pettyCashItemsRepository;

    @PostMapping("/petty-cash-request")
    @PreAuthorize("hasAuthority('create_pettyCashRequest')")
    public ResponseEntity<?> createPettyCashRequest(@Valid @RequestBody List<PettyCashRequestItemsData> data, Authentication authentication, @RequestParam(value = "staffNo", required = false) final String staffNo) {

        Employee employee = null;
        //check first level approver
        if (staffNo == null) {
            String username = authentication.getName();

            User user = service.findUserByUsernameOrEmail(username)
                    .orElseThrow(() -> APIException.badRequest("User not found"));
            employee = employeeService.fetchEmployeeByUser(user);
        } else {
            employee = employeeService.fetchEmployeeByNumberOrThrow(staffNo);
        }

        PettyCashRequests cashRequest = new PettyCashRequests();
        double totalAmount = 0.00;
        String narration = "";

        //generate petty cash request
        cashRequest.setStatus(PettyCashStatus.Pending);
        cashRequest.setTotalAmount(totalAmount);
        cashRequest.setRequestedBy(employee);
//        cashRequest.setRequestDate(LocalDate.now());

        cashRequest.setRequestNo(pettyCashRequestsService.generatepettyCashRequestNo());
        PettyCashRequests cr = pettyCashRequestsService.createCashRequests(cashRequest);
        List<PettyCashRequestItems> cri = new ArrayList<>();
        for (PettyCashRequestItemsData d : data) {
            PettyCashRequestItems item = PettyCashRequestItemsData.map(d);
            item.setAmount(d.getQuantity() * d.getPricePerUnit());
            totalAmount = totalAmount + (d.getQuantity() * d.getPricePerUnit());
            narration = d.getNarration();
            item.setRequestNo(cr);

            cri.add(item);
        }

        cr.setTotalAmount(totalAmount);
        cr.setNarration(narration);

        cr.setApprovalPendingLevel(1);
        //update cr details
        pettyCashRequestsService.createCashRequests(cr);

        //save items
        pettyCashRequestsService.createCashRequestItems(cri);

        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }
//
//    @GetMapping("/petty-cash-request/{requestNo}/approvals")
//    public ResponseEntity<?> fetchPettyCashApprovalsByRequisitionNo(@PathVariable("requestNo") final String requestNo) {
//
//        PettyCashRequests request = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);
//
//        List<PettyCashApprovals> list = approvalsService.fetchPettyCashApprovalsByRequisitionNo(request);//.map(r -> PettyCashRequestsData.map(r));
//        List<PettyCashApprovalsData> dataList = new ArrayList<>();
//        for (PettyCashApprovals a : list) {
//            dataList.add(PettyCashApprovalsData.map(a));
//        }
//        Pager<List<PettyCashApprovalsData>> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Success");
//        pagers.setContent(dataList);
//        PageDetails details = new PageDetails();
//        details.setPage(1);
//        details.setPerPage(dataList.size());
//        details.setTotalElements(Long.valueOf(dataList.size()));
//        details.setTotalPage(1);
//        details.setReportName("Petty cash request approvals");
//        pagers.setPageDetails(details);
//        return ResponseEntity.ok(pagers);
//
//    }
    @GetMapping("/petty-cash-request/me")
    @PreAuthorize("hasAuthority('view_pettyCashRequest')")
    public ResponseEntity<?> findMyPettyCashRequests(Authentication authentication, final Pageable pageable) {
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);

        Page<PettyCashRequestsData> list = pettyCashRequestsService.findPettyCashRequestsByEmployeeWhoRequested(employee, pageable).map(r -> PettyCashRequestsData.map(r));
        Pager<List<PettyCashRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("My petty cash requests");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/petty-cash")
    @PreAuthorize("hasAuthority('view_pettyCashRequest')")
    public ResponseEntity<?> fetchPettyCashRequests(
            @RequestParam(value = "requestNo", required = false) final String requestNo,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "status", required = false) final PettyCashStatus status,
            Pageable pageable) {

        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<PettyCashRequestsData> list = pettyCashRequestsService.findPettyCashRequests(requestNo, null, status, range, pageable).map(r -> PettyCashRequestsData.map(r));
        Pager<List<PettyCashRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Petty cash requests");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/petty-cash-request")
    @PreAuthorize("hasAuthority('view_pettyCashRequest')")
    public ResponseEntity<?> fetchPettyCashRequestsPendingApproval(
            Authentication authentication,
            final Pageable pageable) {
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);

        int loggedInPersonApprovalLevel = approvalConfigService.fetchModuleApproverByModuleAndEmployee(ApprovalModule.PettyCash, employee).getApprovalLevel();

        Page<PettyCashRequestsData> list = pettyCashRequestsService.fetchAllPettyCashRequestsByPendingApprovalLevel(loggedInPersonApprovalLevel, pageable).map(r -> PettyCashRequestsData.map(r));
        Pager<List<PettyCashRequestsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Petty cash requests");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/petty-cash-request/{requestNo}")
    @PreAuthorize("hasAuthority('view_pettyCashRequest')")
    public ResponseEntity<?> fetchPettyCashRequestByNo(@PathVariable("requestNo") final String requestNo, final Pageable pageable) {
        PettyCashRequestsData data = PettyCashRequestsData.map(pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo));
        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(data);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PutMapping("/petty-cash-request/{requestNo}/accept-all")
    @PreAuthorize("hasAuthority('edit_pettyCashRequest')")
    public ResponseEntity<?> acceptAllItems(@PathVariable("requestNo") final String requestNo, Authentication authentication) {
        String username = authentication.getName();

        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
//        Employee employee = employeeService.fetchEmployeeByUser(user);
        PettyCashRequests pettyCashRequest = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);

        PettyCashApprovals approval = new PettyCashApprovals();
        approval.setApprovalStatus(PettyCashStatus.Approved);
        approval.setApprovedBy(user);
        approval.setRequestNo(pettyCashRequest);
        approval = approvalsService.saveCashApproval(approval);

        //check if there is next level approver
        List<ModuleApprovers> approvers = approvalConfigService.fetchModuleApproversByModuleAndLevel(ApprovalModule.PettyCash, pettyCashRequest.getApprovalPendingLevel() + 1);
        if (approvers.size() > 0) {
            //next approvers 
            pettyCashRequest.setApprovalPendingLevel(pettyCashRequest.getApprovalPendingLevel() + 1);
        } else {
            //ready to process
            pettyCashRequest.setApprovalPendingLevel(0);
            pettyCashRequest.setStatus(PettyCashStatus.Approved);
        }
        pettyCashRequestsService.createCashRequests(pettyCashRequest);

        List<PettyCashApprovedItems> approvedItems = new ArrayList<>();
        Double totalAmountApproved = 0.00;

        for (PettyCashRequestItems item : pettyCashRequest.getPettyCashRequestItems()) {
            //insert into approvals table 
            PettyCashApprovedItems approve = new PettyCashApprovedItems();
            approve.setApprovalComments("Accepted");
            approve.setApprovalStatus(PettyCashStatus.Approved);
            approve.setApprovedBy(user);
            approve.setRequestNo(pettyCashRequest);
            approve.setItemNo(item);

            approve.setAmount(item.getAmount());
            approve.setPricePerUnit(item.getPricePerUnit());
            approve.setQuantity(item.getQuantity());
            approve.setApproval(approval);
            //specific approved figures
            approve.setApprovedPricePerUnit(item.getPricePerUnit());
            approve.setApprovedQuantity(item.getQuantity());
            approve.setApprovedAmount((item.getPricePerUnit()* item.getQuantity()));
            totalAmountApproved = totalAmountApproved + approve.getApprovedAmount();

            if (approvers.size() < 1) {
                item.setFinalApprovalStatus(PettyCashStatus.Approved);
                item.setApprovedQuantity(approve.getApprovedQuantity());
                item.setApprovedPricePerUnit(approve.getApprovedPricePerUnit());
                pettyCashItemsRepository.save(item);
            }

            approvedItems.add(approve);
        }

        pettyCashRequest.setApprovedAmount(totalAmountApproved);
        pettyCashRequestsService.createCashRequests(pettyCashRequest);

        List<PettyCashApprovedItems> savedApprovedItems = approvalsService.createItemApproval(approvedItems);

        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Approval response submitted succesfully");
        pagers.setContent(PettyCashRequestsData.map(pettyCashRequest));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }


    @PutMapping("/petty-cash-request/{requestNo}/process-particular-items")
    @PreAuthorize("hasAuthority('edit_pettyCashRequest')")
    public ResponseEntity<?> processParticularItems(
            @RequestBody @Valid  final List<PettyCashProcessedItemsData> pettyCashProcessedItemsData,
            @PathVariable("requestNo") final String requestNo,
            Authentication authentication) {
        String username = authentication.getName();

        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
//        Employee employee = employeeService.fetchEmployeeByUser(user);
        PettyCashRequests pettyCashRequest = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);

        PettyCashApprovals approval = new PettyCashApprovals();
        approval.setApprovalStatus(PettyCashStatus.Approved);
        approval.setApprovedBy(user);
        approval.setRequestNo(pettyCashRequest);
        approval = approvalsService.saveCashApproval(approval);

        //check if there is next level approver
        List<ModuleApprovers> approvers = approvalConfigService.fetchModuleApproversByModuleAndLevel(ApprovalModule.PettyCash, pettyCashRequest.getApprovalPendingLevel() + 1);
        if (approvers.size() > 0) {
            //next approvers
            pettyCashRequest.setApprovalPendingLevel(pettyCashRequest.getApprovalPendingLevel() + 1);
        } else {
            //ready to process
            pettyCashRequest.setApprovalPendingLevel(0);
            pettyCashRequest.setStatus(PettyCashStatus.Approved);
            //update particular item status and approved amount
//            List<PettyCashRequestItems> items = pettyCashRequest.getPettyCashRequestItems();
//            for (PettyCashRequestItems i: items
//                 ) {
//
//            }
        }


        List<PettyCashApprovedItems> approvedItems = new ArrayList<>();
        Double totalAmountApproved = 0.00;
        for (PettyCashProcessedItemsData item : pettyCashProcessedItemsData) {
//validate item
            PettyCashRequestItems requestItem = pettyCashRequestsService.findRequestedItemByIdWithNotFoundDetection(item.getItemId());
            //insert into approvals table
            PettyCashApprovedItems approve = new PettyCashApprovedItems();
            approve.setApprovalComments(item.getApprovalComments()!=null? item.getApprovalComments() : "Accepted");
            approve.setApprovalStatus(PettyCashStatus.Approved);
            approve.setApprovedBy(user);
            approve.setRequestNo(pettyCashRequest);
            approve.setItemNo(requestItem);

            approve.setAmount(item.getAmount());
            approve.setPricePerUnit(item.getPricePerUnit());
            approve.setQuantity(item.getQuantity());
            approve.setApproval(approval);
            //specific approved figures
            approve.setApprovedPricePerUnit(item.getUnitPriceApproved()!=null? item.getUnitPriceApproved(): item.getPricePerUnit() );
            approve.setApprovedQuantity(item.getQuantityApproved()!=null ? item.getQuantityApproved() : item.getQuantity());
            approve.setApprovedAmount((approve.getApprovedPricePerUnit()* approve.getApprovedQuantity()));
            totalAmountApproved = totalAmountApproved + approve.getApprovedAmount();

            if (approvers.size() < 1) {
                requestItem.setFinalApprovalStatus(PettyCashStatus.Approved);
                requestItem.setApprovedQuantity(approve.getApprovedQuantity());
                requestItem.setApprovedPricePerUnit(approve.getApprovedPricePerUnit());
                pettyCashItemsRepository.save(requestItem);
            }
            approvedItems.add(approve);

        }
        pettyCashRequest.setApprovedAmount(totalAmountApproved);
        pettyCashRequestsService.createCashRequests(pettyCashRequest);
        List<PettyCashApprovedItems> savedApprovedItems = approvalsService.createItemApproval(approvedItems);

        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Approval response submitted succesfully");
        pagers.setContent(PettyCashRequestsData.map(pettyCashRequest));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

//    //update petty cash status
//    @PutMapping("/petty-cash-request/{requestNo}/process-item")
//    public ResponseEntity<?> processPettyCashItem(@PathVariable("requestNo") final String requestNo, @Valid @RequestBody List<PettyCashApprovalsData> dataList, Authentication authentication) {
//        PettyCashRequests pettyCashRequests = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);
////        PettyCashRequestItems e = pettyCashRequestsService.findRequestedItemById(itemNo).orElseThrow(() -> APIException.notFound("Item not found << {0} >> ", itemNo));
//        //fetch approval settings by petty cash
//        ApprovalConfig config = approvalConfigService.fetchApprovalConfigByModuleName(ApprovalModule.PettyCash);
//        String username = authentication.getName();
//        User user = service.findUserByUsernameOrEmail(username)
//                .orElseThrow(() -> APIException.badRequest("User not found"));
//        Employee employee = employeeService.fetchEmployeeByUser(user);
//        //validate approver availability
//        ModuleApprovers app = approvalConfigService.fetchModuleApproverByModuleAndEmployee(ApprovalModule.PettyCash, employee);
//        List<PettyCashApprovedItems> approvedItems = new ArrayList<>();
//
//        int noOfRequestedItems = pettyCashRequests.getPettyCashRequestItems().size();
//        int noOfDeclinedItems = 0, noOfAcceptedItems = 0;
//        for (PettyCashApprovalsData data : dataList) {
//
//            PettyCashRequestItems item = pettyCashRequestsService.findRequestedItemById(data.getItemNo()).orElseThrow(() -> APIException.notFound("Item not found << {0} >> ", data.getItemNo()));
//
//            //check if employee has already processed
//            if (approvalsService.findByApprovedByAndRequestNo(user, pettyCashRequests).isPresent()) {
//                throw APIException.conflict("You had already sent your approval response. ", employee.getFullName());
//            }
//
//            if (data.getAmount() > item.getAmount()) {
//                throw APIException.badRequest("You cannot approve amount greater than requested one << {0} >>", item.getAmount());
//            }
//            //insert into approvals table 
//            PettyCashApprovedItems approve = new PettyCashApprovedItems();
//            approve.setApprovalComments(data.getApprovalComments());
//            approve.setApprovalStatus(data.getApprovalStatus());
//            //approve.setApprovedBy(employee);
//            approve.setItemNo(item);
//
//            approve.setAmount(data.getQuantity() * data.getPricePerUnit());
//            approve.setPricePerUnit(data.getPricePerUnit());
//            approve.setQuantity(data.getQuantity());
//
//            if (data.getApprovalStatus().equals(PettyCashStatus.Declined)) {
//                noOfDeclinedItems = noOfDeclinedItems + 1;
//            }
//            if (data.getApprovalStatus().equals(PettyCashStatus.Approved)) {
//                noOfAcceptedItems = noOfAcceptedItems + 1;
//            }
//            approvedItems.add(approve);
//        }
//
//        List<PettyCashApprovedItems> savedApproval = approvalsService.createItemApproval(approvedItems);
//        //update approval levels
//
//        /*
//        1. If all were declined, update the whole petty cash request as declined and send notification to the requester , then update the next approval level to null
//        2. If all were accepted, send the only accepted to next level , if no next level, update the whole petty cash request to approved
//        3. If partly were accepted, send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
//        4. If partly were declined, send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
//         */
//        //1.
////fetch list of the approvals
//        if (noOfDeclinedItems >= noOfRequestedItems) {
//            pettyCashRequests.setApprovalPendingLevel(0);
//            pettyCashRequests.setStatus(PettyCashStatus.Declined);
//        }
//        if (noOfAcceptedItems >= noOfRequestedItems) {
//            pettyCashRequests.setApprovalPendingLevel(0);
//            pettyCashRequests.setStatus(PettyCashStatus.Approved);
//        }
//        if (noOfAcceptedItems > noOfDeclinedItems && noOfAcceptedItems <= noOfRequestedItems) {
//            //send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
//            pettyCashRequests = updatePettyCashRequestsToPartialApproval(pettyCashRequests);
//        }
//        if (noOfAcceptedItems < noOfDeclinedItems && noOfAcceptedItems >= noOfRequestedItems) {
//            //send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
//            pettyCashRequests = updatePettyCashRequestsToPartialApproval(pettyCashRequests);
//        }
//
//        Pager<PettyCashRequestsData> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Approval response submitted succesfully");
//        pagers.setContent(PettyCashRequestsData.map(pettyCashRequests));
//        return ResponseEntity.status(HttpStatus.OK).body(pagers);
//    }
    private PettyCashRequests updatePettyCashRequestsToPartialApproval(PettyCashRequests pettyCashRequests) {
        pettyCashRequests.setStatus(PettyCashStatus.PartiallyApproved);
        //check if there is next level approver
        List<ModuleApprovers> approvers = approvalConfigService.fetchModuleApproversByModuleAndLevel(ApprovalModule.PettyCash, pettyCashRequests.getApprovalPendingLevel() + 1);
        if (approvers.size() > 0) {
            //next approvers 
            pettyCashRequests.setApprovalPendingLevel(pettyCashRequests.getApprovalPendingLevel() + 1);
        } else {
            //ready to process
            pettyCashRequests.setApprovalPendingLevel(0);
        }
        return pettyCashRequests;
    }

}
