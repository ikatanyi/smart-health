/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.payer.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
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
public class PayerService {

    /*
    1. Create payer
    2. Fetch all payers
    3. Update payers details
    4. Remove/Delete payer
     */
    @Autowired
    PayerRepository payerRepository;

    @Transactional
    public Payer createPayer(Payer payer) {
        try {
            return payerRepository.save(payer);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("An error occured while creating payer", e.getMessage());
        }
    }

    public Page<Payer> fetchPayers(final Pageable pageable) {
        return payerRepository.findAll(pageable);
    }

    public Payer updatePayer(Payer payer) {
        try {
            return payerRepository.save(payer);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("There was an error while updating payer id " + payer, e.getMessage());
        }
    }

    public boolean removePayer(Payer payer) {
        try {
            payerRepository.deleteById(payer.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("There was an error in removing payer id " + payer.getId(), e.getMessage());
        }
    }

}
