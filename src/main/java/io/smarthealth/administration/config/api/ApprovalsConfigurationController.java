/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.api;

import io.smarthealth.administration.config.data.ApprovalConfigData;
import io.smarthealth.administration.config.data.ModuleApproversData;
import io.smarthealth.administration.config.domain.ApprovalConfig;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.domain.ModuleApprovers;
import io.smarthealth.administration.config.service.ApprovalConfigService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return ResponseEntity.ok(ApprovalConfigData.map(approvalConfigService.saveApprovalConfig(config)));
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
    public ResponseEntity<?> createApprovalConfiguration(@Valid @RequestBody List<ModuleApproversData> approversData) {
        List<ModuleApprovers> approvers = new ArrayList<>();

        for (ModuleApproversData data : approversData) {
            ModuleApprovers approver = new ModuleApprovers();
            approver.setEmployee(employeeService.fetchEmployeeByNumberOrThrow(data.getStaffNumber()));
            approver.setModuleName(data.getModuleName());
            approver.setPriority(data.getPriority());
            approvers.add(approver);
        }

        List<ModuleApprovers> approver = approvalConfigService.createModuleApprovers(approvers);

        List<ModuleApproversData> data = new ArrayList<>();

        approver.forEach((a) -> {
            ModuleApproversData d = ModuleApproversData.map(a);
            data.add(d);
        });

        return ResponseEntity.ok(data);
    }
}
