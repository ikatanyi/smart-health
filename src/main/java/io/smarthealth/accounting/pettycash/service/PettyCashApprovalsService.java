/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.service;

import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashApprovalsRepository;
import io.smarthealth.organization.facility.domain.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class PettyCashApprovalsService {

    @Autowired
    PettyCashApprovalsRepository pettyCashApprovalsRepository;

    @Transactional
    public List<PettyCashApprovals> createNewApproval(List<PettyCashApprovals> app) {
        return pettyCashApprovalsRepository.saveAll(app);
    }

    public List<PettyCashApprovals> createPettyCashApprovals(List<PettyCashApprovals> a) {
        return pettyCashApprovalsRepository.saveAll(a);
    }

    public List<PettyCashApprovals> fetchPettyCashApprovalsByItemNo(final PettyCashRequestItems p) {
        return pettyCashApprovalsRepository.findByItemNo(p);
    }

    public List<PettyCashApprovals> fetchPettyCashApprovalsByRequisitionNo(final PettyCashRequests request) {
        return pettyCashApprovalsRepository.fetchPettyCashApprovalsByRequestNo(request);
    }

    public Optional<PettyCashApprovals> fetchApproverByEmployeeAndRequestNo(final Employee employee, final PettyCashRequestItems request) {
        return pettyCashApprovalsRepository.findByApprovedByAndItemNo(employee, request);
    }

}
