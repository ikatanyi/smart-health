/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.data.enums.ApprovalModule;
import io.smarthealth.administration.config.domain.ApprovalConfig;
import io.smarthealth.administration.config.domain.ApprovalConfigRepository;
import io.smarthealth.administration.config.domain.ModuleApprovers;
import io.smarthealth.administration.config.domain.ModuleApproversRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class ApprovalConfigService {

    @Autowired
    ApprovalConfigRepository approvalConfigRepository;

    @Autowired
    ModuleApproversRepository moduleApproversRepository;

    @Transactional
    public List<ModuleApprovers> createModuleApprovers(List<ModuleApprovers> approvers) {
        List<ModuleApprovers> savedApprovers = new ArrayList<>();
        for (ModuleApprovers approvers1 : approvers) {
            savedApprovers.add(moduleApproversRepository.save(approvers1));
        }
        return savedApprovers;
    }

    public List<ModuleApprovers> fetchModuleApproversByModule(ApprovalModule module) {
        return moduleApproversRepository.findByModuleName(module);
    }

    public ModuleApprovers fetchModuleApproversByModule(final ApprovalModule module, final Employee employee) {
        return moduleApproversRepository.findByModuleNameAndEmployee(module, employee).orElseThrow(() -> APIException.notFound("No approval rights found", employee.getFullName()));
    }

    public List<ModuleApprovers> fetchModuleApproversByModuleAndLevel(final ApprovalModule module, int level) {
        return moduleApproversRepository.findByModuleNameAndApprovalLevel(module, level);
        //  return moduleApproversRepository.findByModuleNameAndApprovalLevel(module, level).orElseThrow(() -> APIException.notFound("Approver identified by level {0} not found. ", level));
    }

    @Transactional
    public ApprovalConfig saveApprovalConfig(ApprovalConfig config) {
        return approvalConfigRepository.save(config);
    }

    public ApprovalConfig fetchApprovalConfigById(Long id) {
        return approvalConfigRepository.findById(id).orElseThrow(() -> APIException.notFound("Approval configuration settings identified by {0} not found ", id));
    }

    public Page<ApprovalConfig> fetchAllApprovalConfigByModuleName(final ApprovalModule module, final Pageable pageable) {
        return approvalConfigRepository.findByApprovalModule(module, pageable);
    }

    public ApprovalConfig fetchApprovalConfigByModuleName(final ApprovalModule module) {
        return approvalConfigRepository.findByApprovalModule(module).orElseThrow(() -> APIException.notFound("Approval configuration identified by {0} not found ", module.name()));
    }

    public Page<ApprovalConfig> fetchApprovalConfigurations(final Pageable pageable) {
        return approvalConfigRepository.findAll(pageable);
    }
}
