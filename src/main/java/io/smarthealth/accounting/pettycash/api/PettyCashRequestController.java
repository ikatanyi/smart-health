/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.api;

import io.smarthealth.accounting.pettycash.data.PettyCashApprovalsData;
import io.smarthealth.accounting.pettycash.data.PettyCashRequestItemsData;
import io.smarthealth.accounting.pettycash.data.PettyCashRequestsData;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.service.PettyCashApprovalsService;
import io.smarthealth.accounting.pettycash.service.PettyCashRequestsService;
import io.smarthealth.administration.config.data.enums.ApprovalModule;
import io.smarthealth.administration.config.domain.ApprovalConfig;
import io.smarthealth.administration.config.domain.ModuleApprovers;
import io.smarthealth.administration.config.service.ApprovalConfigService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/petty-cash-request")
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

    @GetMapping("/petty-cash-request/{requestNo}/approvals")
    public ResponseEntity<?> fetchPettyCashApprovalsByRequisitionNo(@PathVariable("requestNo") final String requestNo) {

        PettyCashRequests request = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);

        List<PettyCashApprovals> list = approvalsService.fetchPettyCashApprovalsByRequisitionNo(request);//.map(r -> PettyCashRequestsData.map(r));
        List<PettyCashApprovalsData> dataList = new ArrayList<>();
        for (PettyCashApprovals a : list) {
            dataList.add(PettyCashApprovalsData.map(a));
        }
        Pager<List<PettyCashApprovalsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(dataList);
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(dataList.size());
        details.setTotalElements(Long.valueOf(dataList.size()));
        details.setTotalPage(1);
        details.setReportName("Petty cash request approvals");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);

    }

    @GetMapping("/petty-cash-request/me")
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

    @GetMapping("/petty-cash-request")
    public ResponseEntity<?> fetchPettyCashRequestsPendingApproval(Authentication authentication, final Pageable pageable) {
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);

        int level = approvalConfigService.fetchModuleApproversByModule(ApprovalModule.PettyCash, employee).getApprovalLevel();

        Page<PettyCashRequestsData> list = pettyCashRequestsService.fetchAllPettyCashRequestsByPendingApprovalLevel(level, pageable).map(r -> PettyCashRequestsData.map(r));
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
    public ResponseEntity<?> fetchPettyCashRequestByNo(@PathVariable("requestNo") final String requestNo, final Pageable pageable) {
        PettyCashRequestsData data = PettyCashRequestsData.map(pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo));
        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(data);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    //update petty cash status
    @PutMapping("/petty-cash-request/{requestNo}/process")
    public ResponseEntity<?> processPettyCashItem(@PathVariable("requestNo") final String requestNo, @Valid @RequestBody List<PettyCashApprovalsData> dataList, Authentication authentication) {
        PettyCashRequests pettyCashRequests = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);
//        PettyCashRequestItems e = pettyCashRequestsService.findRequestedItemById(itemNo).orElseThrow(() -> APIException.notFound("Item not found << {0} >> ", itemNo));
        //fetch approval settings by petty cash
        ApprovalConfig config = approvalConfigService.fetchApprovalConfigByModuleName(ApprovalModule.PettyCash);
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);
        //validate approver availability
        ModuleApprovers app = approvalConfigService.fetchModuleApproversByModule(ApprovalModule.PettyCash, employee);
        List<PettyCashApprovals> approvedItems = new ArrayList<>();

        int noOfRequestedItems = pettyCashRequests.getPettyCashRequestItems().size();
        int noOfDeclinedItems = 0, noOfAcceptedItems = 0;
        for (PettyCashApprovalsData data : dataList) {

            PettyCashRequestItems item = pettyCashRequestsService.findRequestedItemById(data.getItemNo()).orElseThrow(() -> APIException.notFound("Item not found << {0} >> ", data.getItemNo()));

            //check if employee has already processed
            if (approvalsService.fetchApproverByEmployeeAndRequestNo(employee, item).isPresent()) {
                throw APIException.conflict("You had already sent your approval response. ", employee.getFullName());
            }

            if (data.getAmount() > item.getAmount()) {
                throw APIException.badRequest("You cannot approve amount greater than requested one << {0} >>", item.getAmount());
            }
            //insert into approvals table 
            PettyCashApprovals approve = new PettyCashApprovals();
            approve.setApprovalComments(data.getApprovalComments());
            approve.setApprovalStatus(data.getApprovalStatus());
            approve.setApprovedBy(employee);
            approve.setItemNo(item);

            approve.setAmount(data.getQuantity() * data.getPricePerUnit());
            approve.setPricePerUnit(data.getPricePerUnit());
            approve.setQuantity(data.getQuantity());

            if (data.getApprovalStatus().equals(PettyCashStatus.Declined)) {
                noOfDeclinedItems = noOfDeclinedItems + 1;
            }
            if (data.getApprovalStatus().equals(PettyCashStatus.Approved)) {
                noOfAcceptedItems = noOfAcceptedItems + 1;
            }
            approvedItems.add(approve);
        }

        List<PettyCashApprovals> savedApproval = approvalsService.createNewApproval(approvedItems);
        //update approval levels

        /*
        1. If all were declined, update the whole petty cash request as declined and send notification to the requester , then update the next approval level to null
        2. If all were accepted, send the only accepted to next level , if no next level, update the whole petty cash request to approved
        3. If partly were accepted, send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
        4. If partly were declined, send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
         */
        //1.
//fetch list of the approvals
        if (noOfDeclinedItems >= noOfRequestedItems) {
            pettyCashRequests.setApprovalPendingLevel(0);
            pettyCashRequests.setStatus(PettyCashStatus.Declined);
        }
        if (noOfAcceptedItems >= noOfRequestedItems) {
            pettyCashRequests.setApprovalPendingLevel(0);
            pettyCashRequests.setStatus(PettyCashStatus.Approved);
        }
        if (noOfAcceptedItems > noOfDeclinedItems && noOfAcceptedItems <= noOfRequestedItems) {
            //send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
            pettyCashRequests = updatePettyCashRequestsToPartialApproval(pettyCashRequests);
        }
        if (noOfAcceptedItems < noOfDeclinedItems && noOfAcceptedItems >= noOfRequestedItems) {
            //send the  accepted to next level, if no next level, update the whole petty cash request to partly approved
            pettyCashRequests = updatePettyCashRequestsToPartialApproval(pettyCashRequests);
        }

        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Approval response submitted succesfully");
        pagers.setContent(PettyCashRequestsData.map(pettyCashRequests));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

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
