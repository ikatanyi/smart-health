/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.service;

import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;
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
    public PettyCashApprovals createNewApproval(PettyCashApprovals app) {
        return pettyCashApprovalsRepository.save(app);
    }

    public List<PettyCashApprovals> createPettyCashApprovals(List<PettyCashApprovals> a) {
        return pettyCashApprovalsRepository.saveAll(a);
    }

    public List<PettyCashApprovals> fetchPettyCashApprovalsByRequestNo(final PettyCashRequests p) {
        return pettyCashApprovalsRepository.findByRequestNo(p);
    }

    public Optional<PettyCashApprovals> fetchApproverByEmployeeAndRequestNo(final Employee employee, final PettyCashRequests request) {
        return pettyCashApprovalsRepository.findByEmployeeAndRequestNo(employee, request);
    }

}
