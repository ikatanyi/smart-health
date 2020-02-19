/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.service;

import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashItemsRepository;
import io.smarthealth.accounting.pettycash.domain.repository.PettyCashRequestsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.DateFormatUtil;
import io.smarthealth.organization.facility.domain.Employee;
import java.util.List;
import java.util.Optional;
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
public class PettyCashRequestsService {

    @Autowired
    PettyCashRequestsRepository cashRequestsRepository;

    @Autowired
    PettyCashItemsRepository pettyCashItemsRepository;

    @Transactional
    public PettyCashRequests createCashRequests(PettyCashRequests cashRequest) {
        return cashRequestsRepository.saveAndFlush(cashRequest);
    }

    public PettyCashRequests fetchCashRequestByRequestNo(String requestNo) {
        return cashRequestsRepository.findByRequestNo(requestNo).orElseThrow(() -> APIException.notFound("Request identified by {0} was not found", requestNo));
    }

    public Optional<PettyCashRequestItems> findRequestedItemById(Long id) {
        return pettyCashItemsRepository.findById(id);
    }

    public Page<PettyCashRequests> findPettyCashRequestsByEmployeeWhoRequested(final Employee employee, final Pageable pageable) {
        return cashRequestsRepository.findByRequestedBy(employee, pageable);
    }

    public Page<PettyCashRequests> fetchAllPettyCashRequestsByPendingApprovalLevel(final int level, final Pageable pageable) {
        return cashRequestsRepository.findByApprovalPendingLevel(level, pageable);
    }

    @Transactional
    public List<PettyCashRequestItems> createCashRequestItems(List<PettyCashRequestItems> requestItems) {
        return pettyCashItemsRepository.saveAll(requestItems);
    }

    public List<PettyCashRequestItems> fetchCashRequestItemsByRequest(final PettyCashRequests requestItems) {
        return pettyCashItemsRepository.findByRequestNo(requestItems);
    }

    public String generatepettyCashRequestNo() {
        //Format yyyy-mm-number
        String date = DateFormatUtil.generateDateStringInSpecificFormat("yyyy-MM-dd");
        return date.concat("-" + cashRequestsRepository.count());
    }
}
