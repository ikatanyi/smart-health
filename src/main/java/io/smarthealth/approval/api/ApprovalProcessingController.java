/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.api;

import io.smarthealth.accounting.pettycash.data.PettyCashRequestsData;
import io.smarthealth.accounting.pettycash.service.PettyCashApprovalsService;
import io.smarthealth.accounting.pettycash.service.PettyCashRequestsService;
import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.approval.service.ApprovalConfigService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
public class ApprovalProcessingController {

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

    @GetMapping("/approval-request/{moduleName}")
    public ResponseEntity<?> fetchApprovalRequest(@PathVariable("moduleName") final ApprovalModule moduleName, Authentication authentication, final Pageable pageable) {
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Employee employee = employeeService.fetchEmployeeByUser(user);

        int loggedInPersonApprovalLevel = approvalConfigService.fetchModuleApproverByModuleAndEmployee(ApprovalModule.PettyCash, employee).getApprovalLevel();

        switch (moduleName) {
            case PettyCash:
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
            case StockTransfer:
                break;
            case LPO:
                break;
            default:
                break;
        }
        return null;
    }
//    @GetMapping("/approval-process/{moduleName}")
//    public ResponseEntity<?> fetchApprovalRequest(@PathVariable("moduleName") final ApprovalModule moduleName) {
//
//        return 
//    }

    @PostMapping("/approval-process/{moduleName}")
    public ResponseEntity<?> processRequest(@PathVariable("moduleName") final ApprovalModule moduleName) {
        return ResponseEntity.ok(null);
    }
}
