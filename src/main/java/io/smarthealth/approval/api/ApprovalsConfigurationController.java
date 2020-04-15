/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.api;

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
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/approval-settings")
    public ResponseEntity<?> createApprovalConfiguration(@Valid @RequestBody ApprovalConfigData configData) {
        ApprovalConfig config = ApprovalConfigData.map(configData);
        config.setApprovalModule(configData.getModuleName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApprovalConfigData.map(approvalConfigService.saveApprovalConfig(config)));
    }

    @GetMapping("/approval-settings/{moduleName}")
    public ResponseEntity<?> fetchApprovalConfigurationByModuleName(@PathVariable("moduleName") final ApprovalModule moduleName, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

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
    public ResponseEntity<?> fetchAllApprovalConfiguration(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

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
    public ResponseEntity<?> updateApprovalConfigurations(@PathVariable(value = "id") Long approvalId, @Valid @RequestBody ApprovalConfigData configData) {
        ApprovalConfig config = approvalConfigService.fetchApprovalConfigById(approvalId);

        config.setApprovalModule(configData.getModuleName());
        config.setMinNoOfApprovers(configData.getMinNoOfApprovers());
        config.setNoOfApproveres(configData.getNoOfApproveres());

        return ResponseEntity.ok(ApprovalConfigData.map(approvalConfigService.saveApprovalConfig(config)));
    }

    /* Start of module approvers end points */
    @PostMapping("/module-approver")
    public ResponseEntity<?> addNewModuleApprovers(@Valid @RequestBody List<ModuleApproversData> approversData) {
        List<ModuleApprovers> approvers = new ArrayList<>();

        for (ModuleApproversData data : approversData) {
            ModuleApprovers approver = new ModuleApprovers();
            approver.setEmployee(employeeService.fetchEmployeeByNumberOrThrow(data.getStaffNumber()));
            approver.setModuleName(data.getModuleName());
            approver.setApprovalLevel(data.getApprovalLevel());
            approvers.add(approver);
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
    public ResponseEntity<?> fetchModuleApprovers(@PathVariable("moduleName") final String moduleName) {

        List<ModuleApprovers> entities = approvalConfigService.fetchModuleApproversByModule(ApprovalModule.valueOf(moduleName));
        List<ModuleApproversData> list = new ArrayList<>();
        entities.stream().map((a) -> ModuleApproversData.map(a)).forEachOrdered((d) -> {
            list.add(d);
        });
        Pager<List<ModuleApproversData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list);
        return ResponseEntity.ok(pagers);
    }
}
