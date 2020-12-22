/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.service;

import io.smarthealth.accounting.pettycash.domain.PettyCashApprovedItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashApprovalsRepository;
import io.smarthealth.accounting.pettycash.domain.PettyCashApprovals;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashApprovedItemsRepository;
import io.smarthealth.security.domain.User;

/**
 *
 * @author Simon.waweru
 */
@Service
public class PettyCashApprovalsService {

    @Autowired
    PettyCashApprovedItemsRepository pettyCashApprovedItemsRepository;

    @Autowired
    PettyCashApprovalsRepository pettyCashApprovalsRepository;

    @Transactional
    public List<PettyCashApprovedItems> createItemApproval(List<PettyCashApprovedItems> app) {
        return pettyCashApprovedItemsRepository.saveAll(app);
    }

    @Transactional
    public PettyCashApprovals saveCashApproval(final PettyCashApprovals pca) {
        return pettyCashApprovalsRepository.save(pca);
    }

    public List<PettyCashApprovedItems> createPettyCashApprovals(List<PettyCashApprovedItems> a) {
        return pettyCashApprovedItemsRepository.saveAll(a);
    }

    public List<PettyCashApprovedItems> fetchPettyCashApprovalsByItemNo(final PettyCashRequestItems p) {
        return pettyCashApprovedItemsRepository.findByItemNo(p);
    }

    public List<PettyCashApprovals> fetchPettyCashApprovalsByRequisitionNo(final PettyCashRequests request) {
        return pettyCashApprovalsRepository.findByRequestNo(request);
    }

    public Optional<PettyCashApprovals> findByApprovedByAndRequestNo(final User user, final PettyCashRequests request) {
        return pettyCashApprovalsRepository.findByApprovedByAndRequestNo(user, request);
    }

}
