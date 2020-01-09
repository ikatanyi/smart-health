/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.service;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.infrastructure.exception.APIException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PayerService {

    /*
    1. Create payer
    2. Fetch all payers
    3. Update payers details
    4. Remove/Delete payer
     */ 
    private final PayerRepository payerRepository;

    @Transactional
    public Payer createPayer(Payer payer) {
        return payerRepository.save(payer);
    }

    public Page<Payer> fetchPayers(final Pageable pageable) {
        return payerRepository.findAll(pageable);
    }

    public Payer updatePayer(Payer payer) {
        return payerRepository.save(payer);
    }

    public boolean removePayer(Payer payer) {
        payerRepository.deleteById(payer.getId());
        return true;
    }

    public Payer findPayerByIdWithNotFoundDetection(final Long payerId) {
        return payerRepository.findById(payerId).orElseThrow(() -> APIException.notFound("Payer identified by id {0} no available", payerId));
    }

}
