/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.api;

import static com.mchange.v2.log.MLog.config;
import io.smarthealth.approval.data.ApprovalConfigData;
import io.smarthealth.approval.data.ModuleApproversData;
import io.smarthealth.approval.data.enums.ApprovalModule;
import io.smarthealth.approval.domain.ApprovalConfig;
import io.smarthealth.approval.domain.ModuleApprovers;
import io.smarthealth.approval.service.ApprovalConfigService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api("Api controlling the approval settings for modules such as petty cash and stock transfer")
@RequestMapping("/api")
public class ApprovalsConfigurationController {

    @Autowired
    ApprovalConfigService approvalConfigService;

    @Autowired
    EmployeeService employeeService;
    
    @Autowired
    AuditTrailService auditTrailService; 

    @PostMapping("/approval-settings")
    @PreAuthorize("hasAuthority('create_approvalConfiguration')")
    public ResponseEntity<?> createApprovalConfiguration(@Valid @RequestBody ApprovalConfigData configData) {
        ApprovalConfig config = ApprovalConfigData.map(configData);
        config.setApprovalModule(configData.getModuleName());
        auditTrailService.saveAuditTrail("Approval Settings", "Created Approval Settings for  "+configData.getModuleName().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApprovalConfigData.map(approvalConfigService.saveApprovalConfig(config)));
    }

    @GetMapping("/approval-settings/{moduleName}")
    @PreAuthorize("hasAuthority('view_approvalConfiguration')")
    public ResponseEntity<?> fetchApprovalConfigurationByModuleName(@PathVariable("moduleName") final ApprovalModule moduleName, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        auditTrailService.saveAuditTrail("Approval Settings", "Viewed Approval Settings for  "+moduleName.name());
        Page<ApprovalConfigData> list = approvalConfigService.fetchAllApprovalConfigByModuleName(moduleName, pageable).map(r -> ApprovalConfigData.map(r));
        Pager<List<ApprovalConfigData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Approval settings for " + moduleName);
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/approval-settings")
    @PreAuthorize("hasAuthority('view_approvalConfiguration')")
    public ResponseEntity<?> fetchAllApprovalConfiguration(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        auditTrailService.saveAuditTrail("Approval Settings", "Viewed all Approval Settings ");
        Page<ApprovalConfigData> list = approvalConfigService.fetchApprovalConfigurations(pageable).map(r -> ApprovalConfigData.map(r));
        Pager<List<ApprovalConfigData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Approval settings");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/approval-settings")
    @PreAuthorize("hasAuthority('edit_approvalConfiguration')")
    public ResponseEntity<?> updateApprovalConfigurations(@PathVariable(value = "id") Long approvalId, @Valid @RequestBody ApprovalConfigData configData) {
        ApprovalConfig config = approvalConfigService.fetchApprovalConfigById(approvalId);
        auditTrailService.saveAuditTrail("Approval Settings", "Edited all Approval Settings for "+config.getApprovalModule().name());
        config.setApprovalModule(configData.getModuleName());
        config.setMinNoOfApprovers(configData.getMinNoOfApprovers());
        config.setNoOfApproveres(configData.getNoOfApproveres());

        return ResponseEntity.ok(ApprovalConfigData.map(approvalConfigService.saveApprovalConfig(config)));
    }

    /* Start of module approvers end points */
    @PostMapping("/module-approver")
    @PreAuthorize("hasAuthority('create_approvers')")
    public ResponseEntity<?> addNewModuleApprovers(@Valid @RequestBody List<ModuleApproversData> approversData) {
        List<ModuleApprovers> approvers = new ArrayList<>();

        for (ModuleApproversData data : approversData) {
            ModuleApprovers approver = new ModuleApprovers();
            Employee emp = employeeService.fetchEmployeeByNumberOrThrow(data.getStaffNumber());
            approver.setEmployee(emp);
            approver.setModuleName(data.getModuleName());
            approver.setApprovalLevel(data.getApprovalLevel());
            approvers.add(approver);
            auditTrailService.saveAuditTrail("Approval Settings", "Added "+emp.getFullName()+"  as Approver for "+data.getModuleName());
        }

        List<ModuleApprovers> approver = approvalConfigService.createModuleApprovers(approvers);

        List<ModuleApproversData> data = new ArrayList<>();

        approver.forEach((a) -> {
            ModuleApproversData d = ModuleApproversData.map(a);
            data.add(d);
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @GetMapping("/module-approver/{moduleName}")
    @PreAuthorize("hasAuthority('view_approvers')")
    public ResponseEntity<?> fetchModuleApprovers(@PathVariable("moduleName") final String moduleName) {

        List<ModuleApprovers> entities = approvalConfigService.fetchModuleApproversByModule(ApprovalModule.valueOf(moduleName));
        List<ModuleApproversData> list = new ArrayList<>();
        entities.stream().map((a) -> ModuleApproversData.map(a)).forEachOrdered((d) -> {
            list.add(d);
        });
        auditTrailService.saveAuditTrail("Approval Settings", "viewed approvers for "+moduleName);
        Pager<List<ModuleApproversData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list);
        return ResponseEntity.ok(pagers);
    }
}
