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
import io.smarthealth.auth.domain.User;
import io.smarthealth.auth.service.UserService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
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

    @PostMapping("/petty-cash")
    public ResponseEntity<?> createPettyCash(@Valid @RequestBody List<PettyCashRequestItemsData> data, Authentication authentication, @RequestParam(value = "staffNo", required = false) final String staffNo) {

        Employee employee = null;
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

        //update cr details
        pettyCashRequestsService.createCashRequests(cr);

        //save items
        pettyCashRequestsService.createCashRequestItems(cri);

        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @GetMapping("/petty-cash")
    public ResponseEntity<?> fetchPettyCashRequests(final Pageable pageable) {

        Page<PettyCashRequestsData> list = pettyCashRequestsService.fetchAllPettyCashRequests(pageable).map(r -> PettyCashRequestsData.map(r));
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

    @GetMapping("/petty-cash/{requestNo}")
    public ResponseEntity<?> fetchPettyCashRequestByNo(@PathVariable("requestNo") final String requestNo, final Pageable pageable) {
        PettyCashRequestsData data = PettyCashRequestsData.map(pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo));
        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment successfully Created.");
        pagers.setContent(data);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    //update petty cash status
    @PutMapping("/petty-cash/{requestNo}/process")
    public ResponseEntity<?> processPettyCash(@PathVariable("requestNo") final String requestNo, @Valid @RequestBody PettyCashApprovalsData data, Authentication authentication) {

        PettyCashRequests e = pettyCashRequestsService.fetchCashRequestByRequestNo(requestNo);
        //fetch approval settings by petty cash
        ApprovalConfig config = approvalConfigService.fetchApprovalConfigByModuleName(ApprovalModule.PettyCash);

        String username = authentication.getName();

        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);

        //validate approver availability
        ModuleApprovers app = approvalConfigService.fetchModuleApproversByModule(ApprovalModule.PettyCash, employee);
        //check if employee has already processed
        if (approvalsService.fetchApproverByEmployeeAndRequestNo(employee, e).isPresent()) {
            throw APIException.conflict("You had already sent your approval response. ", employee.getFullName());
        }

        //insert into approvals table 
        PettyCashApprovals approve = new PettyCashApprovals();
        approve.setApprovalComments(data.getApprovalComments());
        approve.setApprovalStatus(data.getApprovalStatus());
        approve.setEmployee(employee);
        approve.setRequestNo(e);
        PettyCashApprovals savedApproval = approvalsService.createNewApproval(approve);
        // fetch list of the approvals
        List<PettyCashApprovals> cashApprovals = approvalsService.fetchPettyCashApprovalsByRequestNo(e);
        if (cashApprovals.size() >= config.getMinNoOfApprovers()) {
            int agreed = 0, declined = 0;
            for (PettyCashApprovals approved : cashApprovals) {
                if (approved.getApprovalStatus().equals(PettyCashStatus.Approved)) {
                    agreed = agreed + 1;
                }
                if (approved.getApprovalStatus().equals(PettyCashStatus.Declined)) {
                    declined = declined + 1;
                }
            }
            if (agreed >= config.getMinNoOfApprovers()) {
                e.setStatus(PettyCashStatus.Approved);
                pettyCashRequestsService.createCashRequests(e);
            }
            if (declined > agreed) {
                e.setStatus(PettyCashStatus.Declined);
                pettyCashRequestsService.createCashRequests(e);
            }
        }
        // e.setStatus(PettyCashStatus.Approved);
//e.set
        Pager<PettyCashRequestsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Approvals");
        pagers.setContent(PettyCashRequestsData.map(e));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
